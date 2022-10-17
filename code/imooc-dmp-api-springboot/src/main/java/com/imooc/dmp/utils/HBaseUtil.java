package com.imooc.dmp.utils;

import com.imooc.dmp.config.HBaseConfig;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Program: dmp
 * Description: HBase工具类
 * Author: Wu
 * Date: 2022/02/10 10:31
 */
@Component
//需要将HBaseConfig优先注入ioc
@DependsOn("HBaseConfig")
public class HBaseUtil {

    @Resource
    private HBaseConfig config;

    private static Connection connection = null;

    private static Admin admin = null;

    //创建HBase连接
    //在对象完全加载后执行
    @PostConstruct
    public void init(){
        if(connection == null){
            try {
                org.apache.hadoop.conf.Configuration hbaseConf = config.getHBaseConf();
                connection = ConnectionFactory.createConnection(hbaseConf);
                admin = connection.getAdmin();
            } catch (IOException e) {
                System.out.println("HBase连接失败！错误信息为：" + e.getMessage());
            }
        }
    }


    /**
     * 根据rowKey查询指定行
     * @param tableName
     * @param rowKey
     * @return
     * @throws IOException
     */
    public Map<String, String> selectOneRowByRowKey(
            String tableName,
            String rowKey) {

        Result rs = null;
        Table table = null;
        Map<String, String> map = new HashMap<>();

        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowKey.getBytes());
            rs = table.get(get);


            for (Cell cell : rs.rawCells()) {
//                String row = Bytes.toString(cell.getRowArray(),
//                        cell.getRowOffset(), cell.getRowLength());
                String column = Bytes.toString(cell.getQualifierArray(),
                        cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(),
                        cell.getValueOffset(), cell.getValueLength());

                map.put("rowKey",rowKey);
                map.put("column",column);
                map.put("value",value);
            }
        }catch (IOException e){
            System.out.println("读取失败"+e.getMessage());
        }
        return map;
    }

    /**
     * 关闭admin
     * @param admin
     * @param rs
     * @param table
     */
//    private void close(Admin admin, Result rs, Table table) {
//        if (admin != null) {
//            try {
//                admin.close();
//            } catch (IOException e) {
//                System.out.println("关闭Admin失败"+e.getMessage());
//            }
//            if (rs != null) {
//                rs.close();
//            }
//            if (table != null) {
//                assert rs != null;
//                rs.close();
//            }
//            if (table != null) {
//                try {
//                    table.close();
//                } catch (IOException e) {
//                    System.out.println("关闭Table失败"+e.getMessage());
//                }
//            }
//        }
//    }

}
