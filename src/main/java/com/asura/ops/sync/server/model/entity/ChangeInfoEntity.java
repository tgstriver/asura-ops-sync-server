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
 * 变更信息
 * </p>
 *
 * @author Mars
 * @since 2022-08-05
 */
@Data
@TableName("change_info")
public class ChangeInfoEntity extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 变更表名
     */
    private String changeTable;

    /**
     * 变更前的字段值
     */
    private String beforeChangeFieldJson;

    /**
     * 变更后的字段值
     */
    private String afterChangeFieldJson;

    /**
     * DDL
     */
    private String ddlSql;

    /**
     * 变更类型：1：insert，2：update，3：delete
     */
    private Integer dmlChangeType;

    /**
     * 1: DDL 2: DML
     */
    private Integer sqlType;


    /**
     * 版本号
     */
    private Date rowversion;


}
