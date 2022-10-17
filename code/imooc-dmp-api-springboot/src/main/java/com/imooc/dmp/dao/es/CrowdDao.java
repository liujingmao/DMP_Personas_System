package com.imooc.dmp.dao.es;

import com.imooc.dmp.entity.es.ESTags;

import java.util.List;

/**
 * 基于es的人群圈选
 */
public interface CrowdDao {

    //单标签查询
    List<ESTags> findBySingleTag(String tagName);

    //复合标签(通过空格分割关键词)查询(and)
    List<ESTags> findByMultiTagsWithAnd(String tagName);

    //复合标签(多个关键词)查询(and)
    public List<ESTags> findByMultiTagsWithMultiKey(String tagName1, String tagName2);

    //复合标签范围查询(range)
    List<ESTags> findByMultiTagsWithRange(String tagName1, String tagName2);

    //复合标签范围查询(大于)
    List<ESTags> findByMultiTagsWithGt(String tagName);

    //复合标签范围查询(大于等于)
    List<ESTags> findByMultiTagsWithGte(String tagName);

    //复合标签范围查询(小于)
    List<ESTags> findByMultiTagsWithLt(String tagName);

    //复合标签范围查询(小于等于)
    List<ESTags> findByMultiTagsWithLte(String tagName);

    //获取查询结果的总数量
}
