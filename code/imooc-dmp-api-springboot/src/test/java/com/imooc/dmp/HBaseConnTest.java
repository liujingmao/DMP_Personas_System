package com.imooc.dmp;

import com.imooc.dmp.config.HBaseConfig;
import com.imooc.dmp.entity.hbase.HBaseT;
import com.imooc.dmp.utils.HBaseUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Program: dmp
 * Description: HBase测试类
 * Author: Wu
 * Date: 2022/02/10 12:13
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HBaseConnTest {

    @Autowired
    private HBaseUtil hbaseUtil;
    @Resource
    private HBaseConfig hbaseConfig;

    //测试是否能获取application.yml的hbase配置
    @Test
    public void testGetHBaseConfProp(){
        Map<String,String> properties =hbaseConfig.getConfProperties();
        System.out.println(properties.get("hbase.zookeeper.quorum"));
    }

    //测试HBase读取
    @Test
    public void testHBaseRead() throws Exception{
        String tableName = "hbase_test";
        String uid = "Imooc-1";
        Map<String, String> res = hbaseUtil.selectOneRowByRowKey(
                tableName,
                uid);
        System.out.println("column="+res.get("column"));
        System.out.println("value="+res.get("value"));

    }
}
