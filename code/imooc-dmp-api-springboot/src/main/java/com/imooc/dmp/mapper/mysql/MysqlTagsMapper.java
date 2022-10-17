package com.imooc.dmp.mapper.mysql;

import com.imooc.dmp.entity.mysql.MysqlTags;
import com.imooc.dmp.entity.mysql.MysqlTagsType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 标签的增删改查
 */
@Repository
public interface MysqlTagsMapper {

    //分页读取标签
    List<MysqlTags> getTagsByPage(Map<String, Object> map);
    //根据标签id读取标签
    MysqlTags getTagById(Integer tag_id);
    //根据标签类型搜索标签
    List<MysqlTags> getTagsByType(MysqlTagsType mysqlTagsType);
    //获取标签第一层分组
    List<MysqlTagsType> getTagsLevel();
    //获取标签第二层分组
    List<MysqlTagsType> getTagsSecLevel(Integer level_id);
    //添加标签
    Integer addTag(MysqlTags mysqlTags);
    //修改标签
    Boolean modifyTag(MysqlTags mysqlTags);
    //删除标签
    Boolean deleteTagById(Integer tag_id);
}
