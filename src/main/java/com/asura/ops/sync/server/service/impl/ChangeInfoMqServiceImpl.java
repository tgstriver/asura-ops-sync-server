package com.asura.ops.sync.server.service.impl;

import com.asura.ops.sync.api.model.ErrorReportDto;
import com.asura.ops.sync.server.mapper.ChangeInfoMqMapper;
import com.asura.ops.sync.server.model.entity.ChangeInfoMqEntity;
import com.asura.ops.sync.server.service.ChangeInfoMqService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author zouyang
 * @description 针对表【change_info_mq(消费消息回调)】的数据库操作Service实现
 * @createDate 2022-08-02 17:12:09
 */
@Service
public class ChangeInfoMqServiceImpl extends ServiceImpl<ChangeInfoMqMapper, ChangeInfoMqEntity>
        implements ChangeInfoMqService {

    @Override
    public void updateConsumerStatus(ErrorReportDto errorReport) {
        ChangeInfoMqEntity changeInfo = new ChangeInfoMqEntity();
        changeInfo.setConsumeStatus(1);
        changeInfo.setConsumeCallbackInfo(errorReport.getErrorMsg());

        LambdaQueryWrapper<ChangeInfoMqEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChangeInfoMqEntity::getMsgId, errorReport.getMsgId());
        queryWrapper.eq(ChangeInfoMqEntity::getClientCode, errorReport.getClientCode());
        queryWrapper.eq(ChangeInfoMqEntity::getSyncDbName, errorReport.getDbName());
        queryWrapper.eq(ChangeInfoMqEntity::getSyncTableName, errorReport.getTableName());


        this.update(changeInfo, queryWrapper);

    }
}




