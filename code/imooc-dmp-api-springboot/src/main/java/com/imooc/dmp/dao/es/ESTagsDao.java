package com.imooc.dmp.dao.es;

import com.imooc.dmp.entity.es.ESTags;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ElasticsearchRepository 继承了 ElasticsearchCrudRepository
 * ElasticsearchRepository封装了ES 的crud操作
 * ElasticsearchRepository的两个泛型：<文档实体类,索引id类型>`
 *`
 */
public interface ESTagsDao extends ElasticsearchRepository<ESTags,String> {

    /**
     * 使用Pageable插件进行es查询分页
     * Pageable 是spring-data定义的接口
     * */

    //查询全部
    @Override
    Page<ESTags> findAll(Pageable pageable);

    //条件查询
    @Override
    Page<ESTags> search(QueryBuilder query, Pageable pageable);

}
