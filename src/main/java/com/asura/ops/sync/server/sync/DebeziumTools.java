package com.asura.ops.sync.server.sync;

import cn.hutool.core.thread.ExecutorBuilder;
import com.asura.common.response.ResponseBase;
import com.asura.ops.sync.server.model.entity.*;
import com.asura.ops.sync.server.mq.DynamicMQ;
import com.asura.ops.sync.server.service.CfgDbService;
import com.asura.ops.sync.server.service.CfgServerDbService;
import com.asura.ops.sync.server.service.CfgServerService;
import com.asura.ops.sync.server.service.CfgTableService;
import com.asura.ops.sync.server.sync.cache.CacheManage;
import com.asura.ops.sync.server.sync.consume.BinlogConsumer;
import com.asura.ops.sync.server.sync.model.OffsetInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.debezium.config.Configuration;
import io.debezium.config.Field;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.runtime.WorkerConfig;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.apache.kafka.connect.storage.OffsetBackingStore;
import org.apache.kafka.connect.storage.OffsetStorageWriter;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author: wangyl
 * @date: 2022/8/3
 * @description: Debezium工具类
 */
@Slf4j
public class DebeziumTools {
    static Map<String, DebeziumEngine> debeziumEngineMap = new HashMap<>();

    static Map<String, Configuration> configurationMap = new HashMap<>();

    static ExecutorService executor = ExecutorBuilder.create().build();
    protected static final Field INTERNAL_KEY_CONVERTER_CLASS = Field.create("internal.key.converter")
            .withDescription("The Converter class that should be used to serialize and deserialize key data for offsets.")
            .withDefault(JsonConverter.class.getName());

    protected static final Field INTERNAL_VALUE_CONVERTER_CLASS = Field.create("internal.value.converter")
            .withDescription("The Converter class that should be used to serialize and deserialize value data for offsets.")
            .withDefault(JsonConverter.class.getName());
    protected static final Field.Set ALL_FIELDS = EmbeddedEngine.CONNECTOR_FIELDS.with(EmbeddedEngine.OFFSET_STORAGE, EmbeddedEngine.OFFSET_STORAGE_FILE_FILENAME,
            EmbeddedEngine.OFFSET_FLUSH_INTERVAL_MS, EmbeddedEngine.OFFSET_COMMIT_TIMEOUT_MS,
            INTERNAL_KEY_CONVERTER_CLASS, INTERNAL_VALUE_CONVERTER_CLASS);

    public static ResponseBase start(String serviceId, boolean isChangeOffset) {
        Configuration embeddedConfig = buildConfiguration(serviceId, isChangeOffset);
        return start(serviceId, embeddedConfig, new BinlogConsumer());
    }

    /**
     * 启动服务
     *
     * @param serviceId      服务id
     * @param embeddedConfig 配置
     * @param consumer       消费者
     * @return void
     * @Date 2022/8/4
     * @Author wangyl
     */
    private static ResponseBase start(String serviceId, Configuration embeddedConfig, Consumer consumer) {
        if (debeziumEngineMap.containsKey(serviceId)) {
            return ResponseBase.fail(serviceId + ":已经启动");
        }

        DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                .using(embeddedConfig.asProperties())
                .notifying(consumer).build();
        executor.execute(engine);

        try {
            List<CfgMqEntity> cacheMqList = ApplicationContextUtil.getObject(CacheManage.class).getCacheMqList();
            ApplicationContextUtil.getObject(DynamicMQ.class).recreateQueue(cacheMqList);
        } catch (ExecutionException e) {
            log.error("创建MQ队列信息失败:{}", e);
        }

        debeziumEngineMap.put(serviceId, engine);

        return ResponseBase.success("start success");
    }

    /**
     * 停止服务
     *
     * @param serviceId 服务id
     * @return void
     * @Date 2022/8/4
     * @Author wangyl
     */
    public static ResponseBase stop(String serviceId) throws IOException {
        DebeziumEngine debeziumEngine = debeziumEngineMap.get(serviceId);
        if (Objects.isNull(debeziumEngine)) {
            return ResponseBase.fail(serviceId + ":未启动");
        }
        debeziumEngine.close();
        debeziumEngineMap.remove(serviceId);
        configurationMap.remove(serviceId);

        return ResponseBase.success(serviceId + ":stop success");
    }

    /**
     * 刷新指定服务
     *
     * @param serviceId 服务id
     * @return void
     * @Date 2022/8/4
     * @Author wangyl
     */
    public static void refresh(String serviceId) throws IOException {
        stop(serviceId);
        start(serviceId, false);
    }

    /**
     * 从指定位置开始同步
     *
     * @param serviceId
     * @param offsetInfo
     * @return void
     * @Date 2022/8/4
     * @Author wangyl
     */
    public static void changeOffset(String serviceId, OffsetInfo offsetInfo) throws IOException {
        offsetInfo.setTs_sec(String.valueOf(System.currentTimeMillis() / 1000));
        Configuration embeddedConfig = buildConfiguration(serviceId, true);
        if (Objects.isNull(embeddedConfig)) {
            throw new RuntimeException("服务没有启动");
        }
        stop(serviceId);
        offsetFlush(embeddedConfig, offsetInfo);
        start(serviceId, true);
    }

    /**
     * 手动写入offset
     *
     * @param embeddedConfig
     * @param offsetInfo
     * @return void
     * @Date 2022/8/4
     * @Author wangyl
     */
    private static void offsetFlush(Configuration embeddedConfig, OffsetInfo offsetInfo) {
        OffsetBackingStore offsetStore = new FileOffsetBackingStore();
        Map<String, String> stringStringMap = embeddedConfig.asMap(ALL_FIELDS);
        stringStringMap.put(WorkerConfig.KEY_CONVERTER_CLASS_CONFIG, JsonConverter.class.getName());
        stringStringMap.put(WorkerConfig.VALUE_CONVERTER_CLASS_CONFIG, JsonConverter.class.getName());
        WorkerConfig workerConfig = new OffsetConfig(stringStringMap);
        offsetStore.configure(workerConfig);
        Map<String, String> embeddedConfigMap = embeddedConfig.asMap();
        String engineName = embeddedConfigMap.get("name");
        String serverName = embeddedConfigMap.get("database.server.name");
        OffsetStorageWriter offsetWriter = new OffsetStorageWriter(offsetStore, engineName,
                keyConverter(embeddedConfig), valueConverter(embeddedConfig));
        offsetStore.start();
        offsetWriter.offset(Map.of("server", serverName), offsetInfo.toMap());
        offsetWriter.beginFlush();
        offsetWriter.doFlush((e, v) -> {
            if (e != null) {
                log.error("offset写入失败", e);
            }
        });
    }

    /**
     * keyConverter
     *
     * @param embeddedConfig
     * @return org.apache.kafka.connect.json.JsonConverter
     * @Date 2022/8/4
     * @Author wangyl
     */
    private static JsonConverter keyConverter(Configuration embeddedConfig) {
        JsonConverter keyConverter = new JsonConverter();
        keyConverter.configure(embeddedConfig.asMap(), true);
        keyConverter.configure(embeddedConfig.subset(INTERNAL_KEY_CONVERTER_CLASS.name() + ".", true).asMap(), true);
        return keyConverter;
    }

    /**
     * valueConverter
     *
     * @param embeddedConfig
     * @return org.apache.kafka.connect.json.JsonConverter
     * @Date 2022/8/4
     * @Author wangyl
     */
    private static JsonConverter valueConverter(Configuration embeddedConfig) {
        JsonConverter valueConverter = new JsonConverter();
        valueConverter.configure(embeddedConfig.asMap(), true);
        embeddedConfig = embeddedConfig.edit().with(INTERNAL_VALUE_CONVERTER_CLASS + ".schemas.enable", false).build();
        valueConverter.configure(embeddedConfig.subset(INTERNAL_VALUE_CONVERTER_CLASS.name() + ".", true).asMap(), false);
        return valueConverter;
    }

    /**
     * 处理Offset的配置
     */
    protected static class OffsetConfig extends WorkerConfig {
        private static final ConfigDef CONFIG;

        static {
            ConfigDef config = baseConfigDef();
            Field.group(config, "file", EmbeddedEngine.OFFSET_STORAGE_FILE_FILENAME);
            Field.group(config, "kafka", EmbeddedEngine.OFFSET_STORAGE_KAFKA_TOPIC);
            Field.group(config, "kafka", EmbeddedEngine.OFFSET_STORAGE_KAFKA_PARTITIONS);
            Field.group(config, "kafka", EmbeddedEngine.OFFSET_STORAGE_KAFKA_REPLICATION_FACTOR);
            CONFIG = config;
        }

        protected OffsetConfig(Map<String, String> props) {
            super(CONFIG, props);
        }
    }


    /**
     * 根据serverId构造Configuration
     *
     * @param serverId
     * @return
     */
    private static Configuration buildConfiguration(String serverId, boolean isChangeOffset) {
        configurationMap.remove(serverId);

        Properties embeddedProperties = new Properties();
        try (InputStream propsInputStream = DebeziumTools.class.getClassLoader().getResourceAsStream("debezium.properties")) {
            embeddedProperties.load(propsInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO:后面优化查询方式
        LambdaQueryWrapper<CfgServerEntity> serverQuery = new LambdaQueryWrapper<CfgServerEntity>();
        serverQuery.eq(CfgServerEntity::getServerCode, serverId);
        CfgServerEntity serverEntity = ApplicationContextUtil.getObject(CfgServerService.class).getOne(serverQuery);
        if (serverEntity == null) {
            log.info("获取同步服务配置为空，退出程序");
            System.exit(9);
        }
        if (isChangeOffset) {
            String histroyFilePath = serverEntity.getHistoryFileName();
            if (histroyFilePath.indexOf("/") == -1) {
                histroyFilePath = System.getProperty("user.dir") + "/" + histroyFilePath;
                File historyFile = new File(histroyFilePath);
                if (historyFile.exists()) {
                    historyFile.delete();
                }
            }
            embeddedProperties.put("snapshot.mode", "schema_only_recovery");
        } else {
            embeddedProperties.put("snapshot.mode", "schema_only");
        }

        CfgDbEntity dbEntity = ApplicationContextUtil.getObject(CfgDbService.class).getById(serverEntity.getCfgDbId());
        if (dbEntity == null) {
            log.info("获取监听DB实例配置为空，退出程序");
            System.exit(9);
        }

        LambdaQueryWrapper<CfgServerDbEntity> serverDbQuery = new LambdaQueryWrapper<CfgServerDbEntity>();
        serverDbQuery.eq(CfgServerDbEntity::getCfgServerId, serverEntity.getId());
        List<CfgServerDbEntity> serverDbList = ApplicationContextUtil.getObject(CfgServerDbService.class).list(serverDbQuery);
        if (CollectionUtils.isEmpty(serverDbList)) {
            log.info("获取监听数据库配置为空，退出程序");
            System.exit(9);
        }

        List<Long> serverDbIds = serverDbList.stream().map(d -> d.getId()).collect(Collectors.toList());

        LambdaQueryWrapper<CfgTableEntity> tableQuery = new LambdaQueryWrapper<CfgTableEntity>();
        tableQuery.in(CfgTableEntity::getCfgServerDbId, serverDbIds);
        List<CfgTableEntity> tableList = ApplicationContextUtil.getObject(CfgTableService.class).list(tableQuery);
        if (CollectionUtils.isEmpty(tableList)) {
            log.info("获取监听数据表配置为空，退出程序");
            System.exit(9);
        }

        embeddedProperties.put("database.serverTimezone","UTC");
        embeddedProperties.put("converters", "timestampConverter");
        embeddedProperties.put("timestampConverter.type", "com.asura.ops.sync.server.sync.converer.TimestampConverter");
        embeddedProperties.put("timestampConverter.format.time", "HH:mm:ss");
        embeddedProperties.put("timestampConverter.format.date", "YYYY-MM-dd");
        embeddedProperties.put("timestampConverter.format.datetime", "YYYY-MM-dd HH:mm:ss");
        embeddedProperties.put("timestampConverter.format.timestamp", "YYYY-MM-dd HH:mm:ss.SSS");

        embeddedProperties.put("decimal.handling.mode", "string");

        embeddedProperties.put("database.tinyInt1isBit", "false");

        embeddedProperties.put("database.hostname", dbEntity.getDbHost());
        embeddedProperties.put("database.port", dbEntity.getDbPort());
        embeddedProperties.put("database.user", dbEntity.getDbUser());
        embeddedProperties.put("database.password", dbEntity.getDbPwd());
        embeddedProperties.put("server.id", dbEntity.getDbServerId());

        embeddedProperties.put("database.server.name", serverEntity.getDbServerName());
        embeddedProperties.put("name", serverEntity.getServerName());
        embeddedProperties.put("offset.storage.file.filename", serverEntity.getOffsetFileName());
        embeddedProperties.put("database.history.file.filename", serverEntity.getHistoryFileName());

        List<String> listenerTables = Lists.newArrayList();
        tableList.forEach(t -> {
            listenerTables.add(t.getCfgServerDbName() + "." + t.getTableName());
        });

        embeddedProperties.put("table.include.list", StringUtils.join(listenerTables, ","));

        Configuration configuration = io.debezium.config.Configuration.from(embeddedProperties);

        configurationMap.put(serverId, configuration);

        return configuration;
    }
}
