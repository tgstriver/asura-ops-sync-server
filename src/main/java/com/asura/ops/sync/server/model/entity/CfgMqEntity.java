package com.asura.ops.sync.server.model.entity;

import com.asura.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 同步队列定义配置
 * </p>
 *
 * @author Mars
 * @since 2022-08-05
 */
@Data
@TableName("cfg_mq")
public class CfgMqEntity extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 同步表配置ID
     */
    private Long syncTableId;

    /**
     * 同步的表名
     */
    private String syncTableName;

    /**
     * 同步的库名
     */
    private String syncDbName;

    /**
     * 同步客户端服务ID
     */
    private Long syncClientId;

    /**
     * 同步客户端服务Code
     */
    private String syncClientCode;

    /**
     * 同步客户端服务名
     */
    private String syncClientName;
    
    /**
     * 交换机名称
     */
    private String exchangeName;

    /**
     * 路由
     */
    private String routeKey;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 版本号
     */
    private Date rowversion;


}
