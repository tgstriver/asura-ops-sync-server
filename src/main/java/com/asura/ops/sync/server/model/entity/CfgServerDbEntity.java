package com.asura.ops.sync.server.model.entity;

import com.asura.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 同步库范围
 * </p>
 *
 * @author Mars
 * @since 2022-08-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cfg_server_db")
public class CfgServerDbEntity extends BaseEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 服务ID
     */
    private Long cfgServerId;

    /**
     * 监听的数据库
     */
    private String dbName;

    /**
     * 版本号
     */
    private Date rowversion;


}
