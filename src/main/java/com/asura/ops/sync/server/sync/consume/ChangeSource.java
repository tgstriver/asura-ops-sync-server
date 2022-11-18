package com.asura.ops.sync.server.sync.consume;

import lombok.Data;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/8
 * @description: 类的描述
 * source :{
 *             "version":"1.9.5.Final",
 *             "connector":"mysql",
 *             "name":"mdm",
 *             "ts_ms":1659929411000,
 *             "snapshot":"false",
 *             "db":"tt",
 *             "sequence":null,
 *             "table":"sku_inventory",
 *             "server_id":10,
 *             "gtid":null,
 *             "file":"binlog.000026",
 *             "pos":127099106,
 *             "row":0,
 *             "thread":476988,
 *             "query":null
 *         },
 */
@Data
public class ChangeSource {

    private String db;

    private String table;

    private String file;

    private long pos;
}
