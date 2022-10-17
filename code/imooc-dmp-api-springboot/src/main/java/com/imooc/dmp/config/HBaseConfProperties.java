package com.imooc.dmp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;


/********************************
 * HBase 配置项
 * *****************************
 */

//获取HBase在application.yml的配置信息
@ConfigurationProperties(prefix = HBaseConfProperties.HBASE_CONF_PREFIX)
public class HBaseConfProperties {

    //HBase配置项前缀
    public static final String HBASE_CONF_PREFIX = "spring";
    //HBase 配置项, 这里要注意和application.yml的名称一致
    //将HBase配置项转换为Map
    private Map<String,String> hbase;

    //getter setter方法
    public Map<String,String> getHbase() {
        return hbase;
    }
    public void setHbase(Map<String,String> hbase) {
        this.hbase = hbase;
    }
}
