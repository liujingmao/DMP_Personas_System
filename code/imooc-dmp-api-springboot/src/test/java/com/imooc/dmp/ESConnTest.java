package com.imooc.dmp;

import com.imooc.dmp.dao.es.ESTagsDao;
import com.imooc.dmp.entity.es.ESTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ESConnTest {

    // 注入标签表操作层 Bean
    @Autowired
    private ESTagsDao ESTagsDao;

    /**
     * 测试es读取
     */
    @Test
    public void testESRead() {
        //打印5条es数据
        Pageable pageable= PageRequest.of(0,5);
        Page<ESTags> pageInfo = ESTagsDao.findAll(pageable);
        List<ESTags> list = pageInfo.getContent();
        for(ESTags tag:list) {
            System.out.println(tag.getUser_id());
            System.out.println(tag.getUser_tags());
            System.out.println("################");
        }
    }
}
