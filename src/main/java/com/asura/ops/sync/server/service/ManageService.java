package com.asura.ops.sync.server.service;

import com.asura.ops.sync.server.model.entity.req.RepushReq;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/10
 * @description: 类的描述
 */
public interface ManageService {
    /**
     * 补偿发送消息
     * @param repushReq
     */
    void repush(RepushReq repushReq);

}
