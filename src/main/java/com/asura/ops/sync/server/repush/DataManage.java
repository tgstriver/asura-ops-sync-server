package com.asura.ops.sync.server.repush;

import com.asura.ops.sync.server.model.entity.CfgDbEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/10
 * @description: 类的描述
 */
@Slf4j
public class DataManage {

    private static String URL = "jdbc:mysql://${host}:${port}/${db}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    public static List<HashMap<String, Object>> queryData(CfgDbEntity dbEntity, DataFilterInfo dataFilterInfo) {
        String realUrl = URL.replace("${host}", dbEntity.getDbHost()).replace("${port}", dbEntity.getDbPort().toString()).replace("${db}", dataFilterInfo.getDbName());
        Connection connection = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(realUrl, dbEntity.getDbUser(), dbEntity.getDbPwd());
            String querySql = "select * from " + dataFilterInfo.getTableName() + " where updated_at >= '" + dataFilterInfo.getStartTime() + "' and updated_at <= '" + dataFilterInfo.getEndTime() + "'";

            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(querySql);

            List<HashMap<String, Object>> result = Lists.newArrayList();
            while (rs.next()) {
                HashMap<String, Object> hashMap = Maps.newHashMap();

                ResultSetMetaData metaData = rs.getMetaData();  //获取列集
                int columnCount = metaData.getColumnCount(); //获取列的数量
                for (int i = 0; i < columnCount; i++) { //循环列
                    String columnName = metaData.getColumnName(i + 1); //通过序号获取列名,起始值为1
                    String columnValue = rs.getString(columnName);  //通过列名获取值.如果列值为空,columnValue为null,不是字符型

                    hashMap.put(columnName, columnValue);
                }
                result.add(hashMap);
            }

            return result;

        } catch (Exception e) {
            log.error("获取数据异常:{}", e);
            return Lists.newArrayList();
        } finally {
            if (stmt != null){
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("stmt关闭异常:{}", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("connection关闭异常:{}", e);
                }
            }

        }
    }

}
