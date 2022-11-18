package com.asura.ops.sync.server.mapper;

import com.asura.ops.sync.server.model.entity.CfgDbEntity;
import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 同步队列定义配置 Mapper 接口
 * </p>
 *
 * @author Mars
 * @since 2022-08-05
 */
public interface CfgMqMapper extends BaseMapper<CfgMqEntity> {

    CfgDbEntity queryDBForMq(@Param("mqEntity") CfgMqEntity mqEntity);
}
