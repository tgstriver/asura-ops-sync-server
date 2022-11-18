package com.asura.ops.sync.server.sync.handler;

import cn.hutool.json.JSONUtil;
import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.asura.ops.sync.server.model.entity.ChangeInfoEntity;
import com.asura.ops.sync.server.model.entity.ChangeInfoMqEntity;
import com.asura.ops.sync.server.service.ChangeInfoMqService;
import com.asura.ops.sync.server.service.ChangeInfoService;
import com.asura.ops.sync.server.sync.consume.ChangePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/8
 * @description: 异步处理消息发送前入库信息， 入库异常未处理
 */
@Component
@Slf4j
public class ChangeEventHandler {


    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private ChangeInfoService changeInfoService;

    @Autowired
    private ChangeInfoMqService changeInfoMqService;

    /**
     * 保存变更信息入库
     *
     * @param payload
     */
    public void asyncHandleChangeInfo(ChangePayload payload) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ChangeInfoEntity changeInfo = new ChangeInfoEntity();
                    changeInfo.setChangeTable(payload.getSource().getTable());
                    changeInfo.setBeforeChangeFieldJson(JSONUtil.toJsonStr(payload.getBefore()));
                    changeInfo.setAfterChangeFieldJson(JSONUtil.toJsonStr(payload.getAfter()));
                    changeInfo.setDmlChangeType(payload.getChangeType());
                    changeInfo.setSqlType(2);

                    changeInfoService.save(changeInfo);
                } catch (Exception ex) {
                    log.error("保存ChangeInfo异常:{}", ex);
                    System.exit(100);
                }
            }
        });
    }

    /**
     * 处理发送前信息入库
     *
     * @param payload
     */
    public void asyncHandleSendInfo(ChangePayload payload, CfgMqEntity mqEntity) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ChangeInfoMqEntity changeInfoMq = new ChangeInfoMqEntity();
                    changeInfoMq.setClientCode(mqEntity.getSyncClientCode());
                    changeInfoMq.setSyncDbName(mqEntity.getSyncDbName());
                    changeInfoMq.setSyncTableName(mqEntity.getSyncTableName());
                    changeInfoMq.setMqInfo(JSONUtil.toJsonStr(payload));
                    changeInfoMq.setMsgId(payload.getMsgId());
                    changeInfoMqService.save(changeInfoMq);
                } catch (Exception ex) {
                    log.error("保存ChangeInfoMq异常:{}", ex);
                    System.exit(100);
                }
            }
        });
    }
}
