package com.asura.ops.sync.server.sync.conf;

import com.asura.log.sdout.QueryLogInterceptor;
import com.asura.ops.sync.server.sync.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.json.JsonConverter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class DebeziumEmbeddedAutoConfiguration implements ApplicationContextAware {

//    @Value("${sync.server.code:mdm}")
//    private String serverCode;
//
//    @Autowired
//    private CfgServerDbService cfgServerDbService;
//
//    @Autowired
//    private CfgServerService cfgServerService;
//
//    @Autowired
//    private CfgDbService cfgDbService;
//
//    @Autowired
//    private CfgTableService cfgTableService;

//    @Bean
//    public Properties embeddedProperties() {
//        Properties propConfig = new Properties();
//        try (InputStream propsInputStream = getClass().getClassLoader().getResourceAsStream("debezium.properties")) {
//            propConfig.load(propsInputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return propConfig;
//    }
//
//    @Bean
//    public io.debezium.config.Configuration embeddedConfig(Properties embeddedProperties) {
//        //TODO:后面优化查询方式
//        LambdaQueryWrapper<CfgServerEntity> serverQuery = new LambdaQueryWrapper<CfgServerEntity>();
//        serverQuery.eq(CfgServerEntity::getServerCode, serverCode);
//        CfgServerEntity serverEntity = cfgServerService.getOne(serverQuery);
//        if (serverEntity == null) {
//            log.info("获取同步服务配置为空，退出程序");
//            System.exit(9);
//        }
//
//        CfgDbEntity dbEntity = cfgDbService.getById(serverEntity.getCfgDbId());
//        if (dbEntity == null) {
//            log.info("获取监听DB实例配置为空，退出程序");
//            System.exit(9);
//        }
//
//        LambdaQueryWrapper<CfgServerDbEntity> serverDbQuery = new LambdaQueryWrapper<CfgServerDbEntity>();
//        serverDbQuery.eq(CfgServerDbEntity::getCfgServerId, serverEntity.getId());
//        List<CfgServerDbEntity> serverDbList = cfgServerDbService.list(serverDbQuery);
//        if (CollectionUtils.isEmpty(serverDbList)) {
//            log.info("获取监听数据库配置为空，退出程序");
//            System.exit(9);
//        }
//
//        List<Long> serverDbIds = serverDbList.stream().map(d -> d.getId()).collect(Collectors.toList());
//
//        LambdaQueryWrapper<CfgTableEntity> tableQuery = new LambdaQueryWrapper<CfgTableEntity>();
//        tableQuery.in(CfgTableEntity::getCfgServerDbId, serverDbIds);
//        List<CfgTableEntity> tableList = cfgTableService.list(tableQuery);
//        if (CollectionUtils.isEmpty(tableList)) {
//            log.info("获取监听数据表配置为空，退出程序");
//            System.exit(9);
//        }
//
//        embeddedProperties.put("database.hostname", dbEntity.getDbHost());
//        embeddedProperties.put("database.port", dbEntity.getDbPort());
//        embeddedProperties.put("database.user", dbEntity.getDbUser());
//        embeddedProperties.put("database.password", dbEntity.getDbPwd());
//        embeddedProperties.put("server.id", dbEntity.getDbServerId());
//
//        embeddedProperties.put("database.server.name", serverEntity.getDbServerName());
//        embeddedProperties.put("name", serverEntity.getServerName());
//        embeddedProperties.put("offset.storage.file.filename", serverEntity.getOffsetFileName());
//        embeddedProperties.put("database.history.file.filename", serverEntity.getHistoryFileName());
//
//        List<String> listenerTables = Lists.newArrayList();
//        tableList.forEach(t -> {
//            listenerTables.add(t.getCfgServerDbName() + "." + t.getTableName());
//        });
//
//        embeddedProperties.put("table.include.list", StringUtils.join(listenerTables, ","));
//
//        return io.debezium.config.Configuration.from(embeddedProperties);
//    }

    @Bean
    public JsonConverter keyConverter() {
        JsonConverter converter = new JsonConverter();
//        converter.configure(embeddedConfig.asMap(), true);
        return converter;
    }

    @Bean
    public JsonConverter valueConverter() {
        JsonConverter converter = new JsonConverter();
//        converter.configure(embeddedConfig.asMap(), false);
        return converter;
    }

    @Bean
    public QueryLogInterceptor queryLogInterceptor() {
        return new QueryLogInterceptor();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.setApplicationContextAware(applicationContext);
    }

    /**
     * 公共线程池
     */
    @Bean
    public ThreadPoolTaskExecutor commonThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        int processNum = Runtime.getRuntime().availableProcessors(); // 返回可用处理器的Java虚拟机的数量
        int corePoolSize = (int) (processNum / (1 - 0.2));
        int maxPoolSize = (int) (processNum / (1 - 0.5));
        pool.setCorePoolSize(corePoolSize); // 核心池大小
        pool.setMaxPoolSize(maxPoolSize); // 最大线程数
        pool.setQueueCapacity(maxPoolSize * 1000000); // 队列程度
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        pool.setThreadPriority(Thread.MAX_PRIORITY);
        pool.setDaemon(false);
        pool.setKeepAliveSeconds(300);// 线程空闲时间
        log.info("processNum:{},corePoolSize:{},maxPoolSize:{},queueCapacity:{}", processNum, corePoolSize, maxPoolSize, maxPoolSize * 1000000);
        return pool;
    }

}
