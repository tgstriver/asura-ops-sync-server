package com.asura.ops.sync.server.sync.consume;

import lombok.Data;

import java.util.HashMap;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/8
 * @description: 类的描述
 */
@Data
public class ChangePayload {

    /**
     * 消息发送ID
     */
    private String msgId;

    /**
     * 变更前的值
     */
    private HashMap<String, Object> before;

    /**
     * 变更后的值
     */
    private HashMap<String, Object> after;

    /**
     * 源信息
     */
    private ChangeSource source;

    /**
     * 变更类型  1:插入 2:更新  3:删除
     */
    private Integer changeType;

    /**
     * 变更时间，入库和接收方自行转换
     */
    private long ts_ms;

    /**
     * 操作类型  c/d/u
     */
    private String op;

}
