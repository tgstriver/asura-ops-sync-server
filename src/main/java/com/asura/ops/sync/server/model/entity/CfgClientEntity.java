package com.asura.ops.sync.server.model.entity;

import com.asura.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 同步客户端服务信息
 * </p>
 *
 * @author Mars
 * @since 2022-08-05
 */
@Data
@TableName("cfg_client")
public class CfgClientEntity extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户端服务编码
     */
    private String clientCode;

    /**
     * 客户端服务名称
     */
    private String clientName;

    /**
     * 同步表配置ID
     */
    private Long syncTableId;

    /**
     * 客户端服务中对应的表名称
     */
    private String clientTableName;

    /**
     * 版本号
     */
    private Date rowversion;


}
