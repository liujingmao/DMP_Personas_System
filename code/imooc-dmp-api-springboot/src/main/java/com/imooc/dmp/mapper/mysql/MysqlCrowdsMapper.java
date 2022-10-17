package com.imooc.dmp.mapper.mysql;

import com.imooc.dmp.entity.mysql.MysqlCrowds;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Program: dmp
 * Description: Mysql人群Mapper
 */
@Repository
public interface MysqlCrowdsMapper {
    //分页读取人群
    List<MysqlCrowds> getCrowdsByPage(Map<String, Object> map);
}
