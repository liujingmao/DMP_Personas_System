package com.imooc.dmp;

import com.alibaba.druid.pool.DruidDataSource;
import com.imooc.dmp.dao.hive.HiveUserDao;
import com.imooc.dmp.entity.hive.HiveUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;


@RunWith(SpringRunner.class)
@SpringBootTest
public class HiveConnTest {

    //指定注入 hiveDataSource Bean
    @Autowired
    @Qualifier("hiveDataSource")
    private DataSource dataSource;
    @Autowired
    private HiveUserDao hiveUserDao;

    /**
     * 测试是否能连接上hive
     * @throws Exception
     */
    @Test
    public void testHiveDataSource() throws Exception{
        Connection connection = dataSource.getConnection();
        System.out.println("#################");
        System.out.println("当前 dataSource 驱动："+ connection.getMetaData().getDriverName());
        System.out.println("#################");
        DruidDataSource druidDataSource = (DruidDataSource) dataSource;
        System.out.println("####### 测试是否能读取 druid 配置 ##########");
        System.out.println("druid设置的最大连接数："+ druidDataSource.getMaxActive());
        System.out.println("Hive 连接成功！");
    }

    @Test
    public void testHiveRead() {
       HiveUser hiveUser = hiveUserDao.getUsers();
       System.out.println(hiveUser.getUser_id());
       System.out.println(hiveUser.getGender());
    }

}
