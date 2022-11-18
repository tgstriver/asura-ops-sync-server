package com.asura.ops.sync.server.service;

import com.asura.ops.sync.api.model.CfgClientDto;
import com.asura.ops.sync.server.model.entity.CfgClientEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
* @author zouyang
* @description 针对表【cfg_client(同步客户端服务信息)】的数据库操作Service
* @createDate 2022-08-02 16:56:17
*/
public interface CfgClientService extends IService<CfgClientEntity> {

    /**
     *
     * @param clientCodes
     * @return
     */
    CfgClientDto getCfgClientDto(Collection<String> clientCodes);

}
