package com.imooc.dmp.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.imooc.dmp.DmpApplication;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/********************************
 * ClickHouse 数据源配置类
 * *****************************
 */

@Configuration
@MapperScan(basePackages = "com.imooc.dmp.mapper.clickhouse",
sqlSessionTemplateRef = "clickhouseSqlSessionTemplate",
nameGenerator = DmpApplication.SpringBeanNameGenerator.class)
public class ClickHouseDataSourceConfig {


    /**
     * 生成 Clickhouse DataSource
     * @param druidDataSourceConfig
     * @return
     */
    @Bean(name = "clickhouseDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.clickhouse")
    public DataSource clickhouseDataSource(DruidDataSourceConfig druidDataSourceConfig){
        DruidDataSource builder =
                DruidDataSourceBuilder.create().build();

        druidDataSourceConfig.setProperties(builder);

        return builder;
    }

    /**
     * 生成sqlSessionFactory
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "clickhouseSqlSessionFactory")
    public SqlSessionFactory clickhouseSqlSessionFactory(
            @Qualifier("clickhouseDataSource") DataSource dataSource) throws Exception{

       SqlSessionFactoryBean sqlSessionFactoryBean =
               new SqlSessionFactoryBean();

       sqlSessionFactoryBean.setDataSource(dataSource);

       return sqlSessionFactoryBean.getObject();
    }


    /**
     * 生成sqlSessionTemplate
     * @param sqlSessionFactory
     * @return
     */
    @Bean(name = "clickhouseSqlSessionTemplate")
    public SqlSessionTemplate clickhouseSqlSessionTemplate(
           @Qualifier("clickhouseSqlSessionFactory") SqlSessionFactory sqlSessionFactory
    ){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
