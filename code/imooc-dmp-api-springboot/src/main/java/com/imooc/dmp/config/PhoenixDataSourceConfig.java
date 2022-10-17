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
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/*************************
 * phoenix数据源 配置类
 * *************************
 */
@Configuration
@MapperScan(basePackages = "com.imooc.dmp.mapper.phoenix",
            sqlSessionTemplateRef = "phoenixSqlSessionTemplate",
nameGenerator = DmpApplication.SpringBeanNameGenerator.class)
public class PhoenixDataSourceConfig {

    /**
     * 生成 Phoenix DataSource
     * @param druidDataSourceConfig
     * @return
     */
    @ConfigurationProperties(prefix = "spring.datasource.druid.phoenix")
    @Bean(name = "phoenixDataSource")
    public DataSource phoenixDataSource(DruidDataSourceConfig druidDataSourceConfig){

        DruidDataSource builder = DruidDataSourceBuilder.create().build();
        druidDataSourceConfig.setProperties(builder);
        return builder;
    }

    /**
     * 生成 SqlSessionFactory
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "phoenixSqlSessionFactory")
    public SqlSessionFactory phoenixSqlSessionFactory(
            @Qualifier("phoenixDataSource") DataSource dataSource)
            throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean
                = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 生成 SqlSessionTemplate
     * @param sqlSessionFactory
     * @return
     * @throws Exception
     */
    @Bean(name = "phoenixSqlSessionTemplate")
    public SqlSessionTemplate phoenixSqlSessionTemplate(
            @Qualifier("phoenixSqlSessionFactory") SqlSessionFactory sqlSessionFactory)
            throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
