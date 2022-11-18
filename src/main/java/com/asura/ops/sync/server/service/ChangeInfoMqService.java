package com.asura.ops.sync.server.service;

import com.asura.ops.sync.api.model.ErrorReportDto;
import com.asura.ops.sync.server.model.entity.ChangeInfoMqEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author zouyang
* @description 针对表【change_info_mq(消费消息回调)】的数据库操作Service
* @createDate 2022-08-02 17:12:09
*/
public interface ChangeInfoMqService extends IService<ChangeInfoMqEntity> {

    /**
     * 更新client端消息失败状态
     * @param errorReport
     */
    void updateConsumerStatus(ErrorReportDto errorReport);


}
