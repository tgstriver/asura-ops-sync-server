package com.asura.ops.sync.server.service.impl;

import cn.hutool.json.JSONUtil;
import com.asura.ops.sync.server.model.entity.CfgDbEntity;
import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.asura.ops.sync.server.model.entity.req.RepushReq;
import com.asura.ops.sync.server.mq.DynamicMQ;
import com.asura.ops.sync.server.repush.DataFilterInfo;
import com.asura.ops.sync.server.repush.DataManage;
import com.asura.ops.sync.server.service.CfgMqService;
import com.asura.ops.sync.server.service.ManageService;
import com.asura.ops.sync.server.sync.consume.ChangePayload;
import com.asura.ops.sync.server.sync.consume.ChangeSource;
import com.asura.ops.sync.server.sync.enums.ChangeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/10
 * @description: 类的描述
 */
@Service
@Slf4j
public class ManageServiceImpl implements ManageService {

    @Autowired
    private DynamicMQ dynamicMQ;

    @Autowired
    private CfgMqService mqService;

    @Async
    @Override
    public void repush(RepushReq repushReq) {
        if (CollectionUtils.isEmpty(repushReq.getClients())) {
            return;
        }
        for (CfgMqEntity mqEntity : repushReq.getClients()) {
            CfgDbEntity dbEntity = mqService.queryDBForMq(mqEntity);

            DataFilterInfo dataFilterInfo = new DataFilterInfo();
            dataFilterInfo.setStartTime(repushReq.getStartTime());
            dataFilterInfo.setEndTime(repushReq.getEndTime());
            dataFilterInfo.setDbName(mqEntity.getSyncDbName());
            dataFilterInfo.setTableName(mqEntity.getSyncTableName());

            List<HashMap<String, Object>> mapList = DataManage.queryData(dbEntity, dataFilterInfo);
            log.info("repush {}条数据", mapList.size());

            for (HashMap<String, Object> map : mapList) {
                ChangePayload payload = new ChangePayload();
                payload.setChangeType(ChangeTypeEnum.REPUSH.getValue());
                payload.setAfter(map);
                payload.setMsgId(UUID.randomUUID().toString());
                payload.setTs_ms(System.currentTimeMillis());

                ChangeSource source = new ChangeSource();
                source.setDb(mqEntity.getSyncDbName());
                source.setTable(mqEntity.getSyncTableName());
                payload.setSource(source);

                String suff = "_all";

                try {
                    dynamicMQ.sendMsg(mqEntity.getExchangeName() + suff, mqEntity.getRouteKey() + "_" + mqEntity.getSyncClientCode(), JSONUtil.toJsonStr(payload));
                } catch (Exception ex) {
                    log.error("补偿发送消息失败:{}", ex);
                }
            }
        }
    }


}
