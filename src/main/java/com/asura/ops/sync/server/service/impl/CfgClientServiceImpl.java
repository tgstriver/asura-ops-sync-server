package com.asura.ops.sync.server.service.impl;

import com.asura.base.response.FeignResponseBase;
import com.asura.ops.sync.api.model.CfgClientDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.asura.ops.sync.server.model.entity.CfgClientEntity;
import com.asura.ops.sync.server.service.CfgClientService;
import com.asura.ops.sync.server.mapper.CfgClientMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author zouyang
 * @description 针对表【cfg_client(同步客户端服务信息)】的数据库操作Service实现
 * @createDate 2022-08-02 16:56:17
 */
@Service
public class CfgClientServiceImpl extends ServiceImpl<CfgClientMapper, CfgClientEntity> implements CfgClientService {

    @Override
    public CfgClientDto getCfgClientDto(Collection<String> clientCodes) {
        CfgClientDto cfgClientDto = baseMapper.getCfgClientList(clientCodes);
        return cfgClientDto;
    }
}




