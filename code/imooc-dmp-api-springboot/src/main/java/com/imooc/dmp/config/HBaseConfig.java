package com.imooc.dmp.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


/********************************
 * HBase 配置类
 * *****************************
 */
@Configuration
@EnableConfigurationProperties(HBaseConfProperties.class)
public class HBaseConfig {

    //HBase配置项类
    private final HBaseConfProperties hBaseConfProperties;

    //构造方法
    public HBaseConfig(HBaseConfProperties hBaseConfProperties){
        this.hBaseConfProperties = hBaseConfProperties;
    }

    //获取HBase配置项
    public Map<String,String> getConfProperties() {
        Map<String,String> conf = hBaseConfProperties.getHbase();
        return conf;
    }

    //将HBase配置项写入到HBaseConfiguration
    @Bean(name = "HBaseConfig")
    public org.apache.hadoop.conf.Configuration getHBaseConf() {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        Map<String,String> properties = hBaseConfProperties.getHbase();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            conf.set(entry.getKey(), entry.getValue());
        }
        return conf;
    }
}
