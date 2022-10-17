package com.imooc.dmp.controller.es;

import com.imooc.dmp.dao.es.ESTagsDao;
import com.imooc.dmp.entity.es.ESTags;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * ****************************
 * ES人群圈选
 * *******************************
 */
//允许跨域
@CrossOrigin
@RestController
@Api(value = "ES-Crowd-API", tags = "ES人群圈选")
public class ESCrowdController {

    @Autowired
    private ESTagsDao ESTagsDao;

    /**
     * 查询所有用户及其被标注的标签
     * @return
     */
    @RequestMapping(value = "/es/search",method = RequestMethod.POST)
    @ApiOperation(value = "查询所有用户及其被标注的标签",notes = "查询所有用户及其被标注的标签")
    public Iterable<ESTags> getUsersAndTags(
            @RequestParam(name = "page",required = false,defaultValue = "0") String page,
            @RequestParam(name = "size",required = false,defaultValue = "10") String size){
        // 获取分页参数
        int pageNum = Integer.parseInt(page);
        int pageSize = Integer.parseInt(size);
        Pageable pageable = PageRequest.of(pageNum,pageSize);
        return ESTagsDao.findAll(pageable);
    }

    /**
     * 单条件人群圈选
     * 以查询 (职业类别:服务业) 为例
     * @param page
     * @param size
     * @param search
     * @return
     */
    @RequestMapping(value = "/es/search/single",method = RequestMethod.POST)
    @ApiOperation(value = "单条件人群圈选",notes = "单条件人群圈选")
    public Iterable<ESTags> searchUserBySingleTag(
            @RequestParam(name = "page",required = false,defaultValue = "0") String page,
            @RequestParam(name = "size",required = false,defaultValue = "10") String size,
            @RequestParam(name = "search",required = true,defaultValue = "职业类别:服务业") String search){
        // 获取分页参数
        int pageNum = Integer.parseInt(page);
        int pageSize = Integer.parseInt(size);
        Pageable pageable = PageRequest.of(pageNum,pageSize);
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        bqb.must(QueryBuilders.matchPhraseQuery("user_tags", search));
        return ESTagsDao.search(bqb,pageable);
    }

    /**
     * 多条件人群圈选(and)
     * 以查询 (浏览时间段:偏好下午浏览 且 职业级别:老板) 为例
     * @param page
     * @param size
     * @param search
     * @return
     */
    @RequestMapping(value = "/es/search/and",method = RequestMethod.POST)
    @ApiOperation(value = "多条件(and)人群圈选",notes = "多条件(and)人群圈选")
    public Iterable<ESTags> searchUserByTagsWithAnd(
            @RequestParam(name = "page",required = false,defaultValue = "0") String page,
            @RequestParam(name = "size",required = false,defaultValue = "10") String size,
            @RequestParam(name = "search",required = true,defaultValue = "浏览时间段:偏好下午浏览|职业级别:老板") String search){
        // 获取分页参数
        int pageNum = Integer.parseInt(page);
        int pageSize = Integer.parseInt(size);
        Pageable pageable = PageRequest.of(pageNum,pageSize);
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        bqb.must(QueryBuilders.matchQuery("user_tags", search));
        return ESTagsDao.search(bqb,pageable);
    }
}
