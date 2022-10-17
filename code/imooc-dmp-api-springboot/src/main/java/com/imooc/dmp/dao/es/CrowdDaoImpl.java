package com.imooc.dmp.dao.es;

import com.imooc.dmp.entity.es.ESTags;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrowdDaoImpl implements CrowdDao {

    private final static String FIELD_USER_TAGS = "user_tags";

    //注入ElasticsearchTemplate对象
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    //es的查询有match 和 term
    //match属于模糊查询，而且会将搜索词语进行分词
    //term属于精确查询，而且不会将搜索词语进行分词

    /**
     *
     * NativeSearchQuery: Spring-data-es 的查询对象
     * NativeSearchQueryBuilder： 构造出NativeSearchQuery对象
     * QueryBuilders: 设置查询条件 是es的对象
     * SortBuilders: 设置排序条件 是es的对象
     * HighlightBuilder: 设置高亮条件 是es的对象
     * QueryBuilders.matchQuery: 关键字分词，精确查询
     * QueryBuilders.termQuery：关键字不分词，精确查询
     */

    /**
     * 单标签查询
     * @param tagName
     * @return
     */
    @Override
    public List<ESTags> findBySingleTag(String tagName) {

        //注意: 这里和有视频的由些出入
        NativeSearchQuery queryBuilder =
                new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(FIELD_USER_TAGS,tagName))
                .build();
        List<ESTags> tags =
                elasticsearchTemplate.queryForList(queryBuilder, ESTags.class);

        return tags;
    }

    /**
     * 复合标签(通过空格分割关键词)查询(and)
     * @param tagName
     * @return
     */
    @Override
    public List<ESTags> findByMultiTagsWithAnd(String tagName) {

        //注意: 这里和有视频的由些出入
        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(FIELD_USER_TAGS,tagName))
                .build();
        List<ESTags> tags = elasticsearchTemplate.queryForList(queryBuilder, ESTags.class);
        return tags;
    }

    //视频没有讲到这个方法
    /**
     * 复合标签(多个关键词)查询(and)
     * 这里只演示两个关键词
     * @param tagName1
     * @param tagName2
     * @return
     */
    @Override
    public List<ESTags> findByMultiTagsWithMultiKey(String tagName1, String tagName2) {

        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery(FIELD_USER_TAGS,tagName1))
                        .should(QueryBuilders.termQuery(FIELD_USER_TAGS,tagName2))
                )
                .build();
        List<ESTags> tags = elasticsearchTemplate.queryForList(queryBuilder, ESTags.class);
        return tags;
    }

    /**
     * 复合标签范围查询(range) 闭区间，若为开区间，from函数第二个参数是false
     * @param tagName1
     * @param tagName2
     * @return
     */
    @Override
    public List<ESTags> findByMultiTagsWithRange(String tagName1, String tagName2) {
        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders
                        .rangeQuery(FIELD_USER_TAGS)
                        //闭区间查询
                        .from(tagName1)
                        //开区间查询
                        //.from(tagName1,false)
                        .to(tagName2)
                )
                .build();
        List<ESTags> tags = elasticsearchTemplate.queryForList(queryBuilder, ESTags.class);
        return tags;
    }

    /**
     * 复合标签范围查询(大于)
     * @param tagName
     * @return
     */
    @Override
    public List<ESTags> findByMultiTagsWithGt(String tagName) {
        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders
                        .rangeQuery(FIELD_USER_TAGS)
                        .gt(tagName))
                .build();
        List<ESTags> tags = elasticsearchTemplate.queryForList(queryBuilder, ESTags.class);
        return tags;
    }

    /**
     * 复合标签范围查询(大于等于`)
     * @param tagName
     * @return
     */
    @Override
    public List<ESTags> findByMultiTagsWithGte(String tagName) {
        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders
                        .rangeQuery(FIELD_USER_TAGS)
                        .gte(tagName))
                .build();
        List<ESTags> tags = elasticsearchTemplate.queryForList(queryBuilder, ESTags.class);
        return tags;
    }

    /**
     * 复合标签范围查询(小于)
     * @param tagName
     * @return
     */
    @Override
    public List<ESTags> findByMultiTagsWithLt(String tagName) {
        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders
                        .rangeQuery(FIELD_USER_TAGS)
                        .lt(tagName))
                .build();
        List<ESTags> tags = elasticsearchTemplate.queryForList(queryBuilder, ESTags.class);
        return tags;
    }

    /**
     * 复合标签范围查询(小于等于)
     * @param tagName
     * @return
     */
    @Override
    public List<ESTags> findByMultiTagsWithLte(String tagName) {
        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders
                        .rangeQuery(FIELD_USER_TAGS)
                        .lte(tagName))
                .build();
        List<ESTags> tags = elasticsearchTemplate.queryForList(queryBuilder, ESTags.class);
        return tags;
    }
}
