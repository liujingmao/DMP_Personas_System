package com.imooc.dmp.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/********************************
 * druid 配置类
 * *****************************
 */
@Configuration
public class DruidDataSourceConfig {

    @Value("${spring.datasource.druid.max-active}")
    private int maxActive;

    /**
     * 配置 druid
     * @param druidDataSource
     * @return
     */
    public DruidDataSource setProperties(DruidDataSource druidDataSource){
        druidDataSource.setMaxActive(maxActive);
        return druidDataSource;
    }


}
