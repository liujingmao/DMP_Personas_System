package com.imooc.dmp;

import com.alibaba.druid.pool.DruidDataSource;
import com.imooc.dmp.entity.clickhouse.ClickHouseT;
import com.imooc.dmp.entity.phoenix.PhoenixT;
import com.imooc.dmp.mapper.clickhouse.ClickHouseTMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ClickHouseTest {

    //指定注入 clickhouseDataSource Bean
    @Autowired
    @Qualifier("clickhouseDataSource")
    private DataSource dataSource;
    @Autowired
    private ClickHouseTMapper clickHouseTMapper;

    /**
     * 测试是否能连接上clickhouse
     * @throws Exception
     */
    @Test
    public void testCHDataSource() throws Exception{
        Connection connection = dataSource.getConnection();
        System.out.println("#################");
        System.out.println("当前 dataSource 驱动："+ connection.getMetaData().getDriverName());
        System.out.println("#################");
        DruidDataSource druidDataSource = (DruidDataSource) dataSource;
        System.out.println("####### 测试是否能读取 druid 配置 ##########");
        System.out.println("druid设置的最大连接数："+ druidDataSource.getMaxActive());
        System.out.println("ClickHouse 连接成功！");
    }

    @Test
    public void testCHRead(){
        List<ClickHouseT> list = clickHouseTMapper.getClickHouseT();
        for(ClickHouseT clickHouseT :list ){
            System.out.println(clickHouseT.getName());
            System.out.println("##############");
        }
    }

}
