package com.asura.ops.sync.server.mapper;

import com.asura.ops.sync.api.model.CfgClientDto;
import com.asura.ops.sync.server.model.entity.CfgClientEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author zouyang
 * @description 针对表【cfg_client(同步客户端服务信息)】的数据库操作Mapper
 * @createDate 2022-08-02 16:56:17
 * @Entity com.asura.ops.sync.server.model.entity.CfgClientEntity
 */
public interface CfgClientMapper extends BaseMapper<CfgClientEntity> {

    /**
     * 获取客户端配置
     *
     * @param clientCodes
     * @return
     */
    CfgClientDto getCfgClientList(@Param("clientCodes") Collection<String> clientCodes);
}




