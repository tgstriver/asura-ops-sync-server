package com.asura.ops.sync.server.model.entity;

import com.asura.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 同步服务进程信息
 * </p>
 *
 * @author Mars
 * @since 2022-08-05
 */
@Data
@TableName("cfg_server")
public class CfgServerEntity extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 同步服务编码（比如：mdm）
     */
    private String serverCode;

    /**
     * 同步服务名称，对应name
     */
    private String serverName;

    /**
     * 同步服务中文名称（MDM同步服务）
     */
    private String serverNameCh;

    /**
     * 同步数据库ID
     */
    private Long cfgDbId;

    /**
     * 同步数据库名，对应database.server.name，对应offset.storage.file.filename
     */
    private String dbServerName;

    /**
     * offset文件名
     */
    private String offsetFileName;

    /**
     * history文件名，对应database.history.file.filename
     */
    private String historyFileName;

    /**
     * 版本号
     */
    private Date rowversion;


}
