package com.imooc.dmp.dao.hive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Dao的基础类
 */
public class HiveBaseDao {

    @Autowired
    @Qualifier("hiveJdbcTemplate")
    private JdbcTemplate hiveJdbcTemplate;

    /**
     * 获取JdbcTemplate
     * @return
     */
    public JdbcTemplate getHiveJdbcTemplate(){
        return hiveJdbcTemplate;
    }
}
