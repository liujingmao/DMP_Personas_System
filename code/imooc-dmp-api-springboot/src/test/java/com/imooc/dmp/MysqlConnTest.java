package com.imooc.dmp;

import com.alibaba.druid.pool.DruidDataSource;
import com.imooc.dmp.entity.mysql.MysqlTags;
import com.imooc.dmp.mapper.mysql.MysqlTagsMapper;
import com.imooc.dmp.utils.PageUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MysqlConnTest {

    //指定注入 mysqlDataSource Bean
    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource dataSource;
    //注入 Mysql Mapper
    @Autowired
    private MysqlTagsMapper mysqlTagsMapper;

    /**
     * 测试是否能连接上mysql
     * @throws Exception
     */
    @Test
    public void testMysqlDataSource() throws Exception{
        Connection connection = dataSource.getConnection();
        System.out.println("#################");
        System.out.println("当前 dataSource 驱动："+ connection.getMetaData().getDriverName());
        System.out.println("#################");
        DruidDataSource druidDataSource = (DruidDataSource) dataSource;
        System.out.println("####### 测试是否能读取 druid 配置 ##########");
        System.out.println("druid设置的最大连接数："+ druidDataSource.getMaxActive());
        System.out.println("Mysql 连接成功！");
    }

    /**
     * 读取前5条标签信息
     */
    @Test
    public void testMysqlRead(){
        int page = 1;
        int size = 5;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page",(page-1)*size);
        params.put("size",size);
        List<MysqlTags> list = mysqlTagsMapper.getTagsByPage(params);
        PageUtil<MysqlTags> pager = new PageUtil<MysqlTags>();
        pager.setData(list);
        for(MysqlTags tags: pager.getData()){
            System.out.println(tags.getTag_id());
            System.out.println(tags.getTag_name());
            System.out.println(tags.getTag_name_cn());
            System.out.println("#################");
        }
    }

}
