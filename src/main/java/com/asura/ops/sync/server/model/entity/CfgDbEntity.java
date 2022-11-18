package com.asura.ops.sync.server.model.entity;

import com.asura.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 监听数据库实例信息
 * </p>
 *
 * @author Mars
 * @since 2022-08-05
 */
@Data
@TableName("cfg_db")
public class CfgDbEntity extends BaseEntity {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主机地址
     */
    private String dbHost;

    /**
     * 主机端口
     */
    private Integer dbPort;

    /**
     * 用户名
     */
    private String dbUser;

    /**
     * 密码（密文）
     */
    private String dbPwd;

    /**
     * 监听的Mysql实例server.id
     */
    private Integer dbServerId;

    /**
     * 版本号
     */
    private Date rowversion;


}
