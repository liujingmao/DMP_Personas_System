package com.imooc.dmp.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/************************
 * Hive数据源 配置类
 * ********************
 */
@Configuration
public class HiveDataSourceConfig {


    /**
     * 生成 Hive DataSource
     * @param druidDataSourceConfig
     * @return
     */
    @ConfigurationProperties(prefix = "spring.datasource.druid.hive")
    @Bean(name = "hiveDataSource")
    public DataSource hiveDataSource(DruidDataSourceConfig druidDataSourceConfig){

        DruidDataSource builder = DruidDataSourceBuilder.create().build();
        druidDataSourceConfig.setProperties(builder);
        return builder;
    }
}
