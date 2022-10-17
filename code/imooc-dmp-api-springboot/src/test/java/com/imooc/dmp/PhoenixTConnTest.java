package com.imooc.dmp;

import com.alibaba.druid.pool.DruidDataSource;
import com.imooc.dmp.entity.phoenix.PhoenixT;
import com.imooc.dmp.mapper.phoenix.PhoenixTMapper;
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
public class PhoenixTConnTest {

    @Autowired
    @Qualifier("phoenixDataSource")
    private DataSource dataSource;
    @Autowired
    private PhoenixTMapper phoenixTMapper;

    /**
     * 测试通过phoenix driver是否能连接上hbase
     * @throws Exception
     */
    @Test
    public void testPhoenixDataSource() throws Exception{
        Connection connection = dataSource.getConnection();
        System.out.println("#################");
        System.out.println("当前 dataSource 驱动："+ connection.getMetaData().getDriverName());
        System.out.println("#################");
        DruidDataSource druidDataSource = (DruidDataSource) dataSource;
        System.out.println("####### 测试是否能读取 druid 配置 ##########");
        System.out.println("druid设置的最大连接数："+ druidDataSource.getMaxActive());
        System.out.println("Phoenix 连接成功！");
    }

    @Test
    public void testPhoenixRead() {
        List<PhoenixT> list = phoenixTMapper.getPhoenixT();
        for(PhoenixT PhoenixT :list ){
            System.out.println(PhoenixT.getId());
            System.out.println(PhoenixT.getName());
            System.out.println("##############");
        }
    }

//    @Test
//    public void testConnToHBaseWithoutDriver() throws Exception{
//        String url = "jdbc:phoenix:192.168.137.33:2182";
//        Connection conn = null;
//        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
//        System.out.println("Connecting to database..");
//        conn = DriverManager.getConnection(url);
//        System.out.println(conn);
//    }

}
