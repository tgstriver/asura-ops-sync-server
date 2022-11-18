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
 * 同步表范围信息
 * </p>
 *
 * @author Mars
 * @since 2022-08-05
 */
@Data
@TableName("cfg_table")
public class CfgTableEntity extends BaseEntity {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 同步数据库ID
     */
    private Long cfgServerDbId;

    /**
     * 同步数据库名
     */
    private String cfgServerDbName;

    /**
     * 同步表名
     */
    private String tableName;

    /**
     * 版本号
     */
    private Date rowversion;


}
