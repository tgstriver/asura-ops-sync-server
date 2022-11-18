package com.asura.ops.sync.server.sync.consume;

import cn.hutool.json.JSONUtil;
import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.asura.ops.sync.server.mq.DynamicMQ;
import com.asura.ops.sync.server.sync.ApplicationContextUtil;
import com.asura.ops.sync.server.sync.cache.CacheManage;
import com.asura.ops.sync.server.sync.enums.ChangeTypeEnum;
import com.asura.ops.sync.server.sync.handler.ChangeEventHandler;
import com.google.common.collect.Lists;
import io.debezium.engine.ChangeEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/5
 * @description: 类的描述
 */
@Slf4j
public class BinlogConsumer implements Consumer {

    private CacheManage cacheManage;

    private DynamicMQ dynamicMQ;

    private ChangeEventHandler changeEventHandler;

    public BinlogConsumer() {
        //初始化缓存
        cacheManage = ApplicationContextUtil.getObject(CacheManage.class);
        dynamicMQ = ApplicationContextUtil.getObject(DynamicMQ.class);
        changeEventHandler = ApplicationContextUtil.getObject(ChangeEventHandler.class);
    }

    @Override
    public void accept(Object o) {
        ChangeEvent changeEvent = (ChangeEvent) o;
        if (changeEvent.value() == null){
            log.info("ChangeEvent value is null");
            return;
        }

        ChangeEntity changeEntity = JSONUtil.toBean(changeEvent.value().toString(), ChangeEntity.class);

        if (changeEntity.getPayload().getOp().equals("r")){
            //read场景，第一次启动时候要全量read一遍
            log.info("全量消息：{}", JSONUtil.toJsonStr(changeEntity.getPayload().getSource()));
            return;
        }

        if (changeEntity.getPayload().getAfter() == null) {
            //删除
            changeEntity.getPayload().setChangeType(ChangeTypeEnum.DELETE.getValue());
        } else if (changeEntity.getPayload().getBefore() == null) {
            //插入
            changeEntity.getPayload().setChangeType(ChangeTypeEnum.INSERT.getValue());

        } else if (changeEntity.getPayload().getBefore() != null && changeEntity.getPayload() != null) {
            //更新
            changeEntity.getPayload().setChangeType(ChangeTypeEnum.UPDATE.getValue());
        }

        log.info("增量消息：{}", JSONUtil.toJsonStr(changeEntity.getPayload()));

        try {
            //说明：由于异步入库，对发送失败或成功没有做再次确认处理
            changeEventHandler.asyncHandleChangeInfo(changeEntity.getPayload());

            List<CfgMqEntity> cacheMqList = cacheManage.getCacheMqList();
            //过滤关注此次数据变更表的client mq列表
            List<CfgMqEntity> sendMqList = cacheMqList.stream().filter(m -> changeEntity.getPayload().getSource().getTable().equals(m.getSyncTableName())).collect(Collectors.toList());

            //根据exchange + routekey 去重
            HashMap<String, List<CfgMqEntity>> filterRouteKeyMqMap = new HashMap<>();
            for (CfgMqEntity mqEntity : sendMqList) {
                String key = mqEntity.getExchangeName() + "_" + mqEntity.getRouteKey();
                if (!filterRouteKeyMqMap.containsKey(key)) {
                    filterRouteKeyMqMap.put(key, Lists.newArrayList(mqEntity));
                } else {
                    filterRouteKeyMqMap.get(key).add(mqEntity);
                }
            }

            for (String key : filterRouteKeyMqMap.keySet()) {
                changeEntity.getPayload().setMsgId(UUID.randomUUID().toString());

                for (CfgMqEntity mqEntity : filterRouteKeyMqMap.get(key)) {
                    changeEventHandler.asyncHandleSendInfo(changeEntity.getPayload(), mqEntity);
                }

                //发送 payload
                dynamicMQ.sendMsg(filterRouteKeyMqMap.get(key).get(0).getExchangeName(), filterRouteKeyMqMap.get(key).get(0).getRouteKey(), JSONUtil.toJsonStr(changeEntity.getPayload()));
            }
        } catch (ExecutionException e) {
            log.error("发送消息失败:{}", e);
        }
    }
}
