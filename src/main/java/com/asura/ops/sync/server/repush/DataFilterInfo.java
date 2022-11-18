package com.asura.ops.sync.server.repush;

import lombok.Data;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/10
 * @description: 类的描述
 */
@Data
public class DataFilterInfo {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;


    private String dbName;

    private String tableName;


}
