package com.asura.ops.sync.server.sync.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class OffsetInfo {
    /**
     * 修改时间，前端不传，后端去当前系统时间
     */
    String ts_sec;
    /**
     * binlog 文件名
     */
    String file;

    /**
     * binlog 位置
     */
    String pos;
    String gtids;

    /**
     * 转为map
     *
     * @param
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Date 2022/8/4
     * @Author wangyl
     */
    public Map<String, Object> toMap() {
        Map<String, Object> param = new HashMap(4);
        param.put("ts_sec", ts_sec);
        param.put("file", file);
        param.put("pos", pos);
        param.put("gtids", gtids);
        return param;
    }
}
