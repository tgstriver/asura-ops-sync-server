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
 * 消费消息回调
 * </p>
 *
 * @author Mars
 * @since 2022-08-05
 */
@Data
@TableName("change_info_mq")
public class ChangeInfoMqEntity extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送消息ID
     *
     */
    private String msgId;

    /**
     * 同步的表名
     */
    private String syncTableName;

    /**
     * 同步的库名
     */
    private String syncDbName;

    /**
     * 同步客户端服务Code
     */
    private String clientCode;

    /**
     * mq内容
     */
    private String mqInfo;

    /**
     * 发送状态，0: 发送失败 1：发送成功
     */
    private Integer sendStatus;

    /**
     * 消费状态，0：消费失败，1：消费成功
     */
    private Integer consumeStatus;

    /**
     * 消费回调信息
     */
    private String consumeCallbackInfo;

    /**
     * 版本号
     */
    private Date rowversion;


}
