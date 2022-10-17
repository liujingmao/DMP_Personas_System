package com.imooc.dmp.controller.mysql;

import com.imooc.dmp.entity.enums.ResultCode;
import com.imooc.dmp.entity.mysql.MysqlTags;
import com.imooc.dmp.entity.mysql.MysqlTagsType;
import com.imooc.dmp.mapper.mysql.MysqlTagsMapper;
import com.imooc.dmp.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * *****************************
 * Mysql标签管理 (标签的增删改查)
 ********************************
 */
//允许跨域
@CrossOrigin
@RestController
@Api(value = "Mysql-Tags-API", tags = "Mysql标签管理")
public class MysqlTagsManagerController {

    @Autowired
    private MysqlTagsMapper mysqlTagsMapper;

    /**
     *
     * Springboot的参数传递：
     * 1.通过@RequestBody接收vue的传过来的json,
     *   因为@RequestBody接收的是标签对象，所以这个json必须和标签对象的属性相匹配
     *
     * 2.通过@PathVariable接收url的传参
     *
     */


    /**
     * 查询标签列表(分页)
     * @return
     */
    @RequestMapping(value = "/tag-manager/tags/{page}",
            method = RequestMethod.GET)
    @ApiOperation(value = "查询标签列表(分页)",notes = "查询标签列表(分页)")
    public Response<List<MysqlTags>> getTagsByPage(@PathVariable("page") Integer page){
        Map<String, Object> params = new HashMap<String, Object>();
        int size = 5;
        params.put("page",(page-1)*size);
        params.put("size",size);
        List<MysqlTags> tags = mysqlTagsMapper.getTagsByPage(params);
        return Response.success(tags);
    }


    /**
     * 根据tag_id搜索标签
     * @param id
     * @return
     */
    @RequestMapping(value = "/tag-manager/tag/{id}",
            method = RequestMethod.GET)
    @ApiOperation(value = "根据tag_id搜索标签",notes = "根据tag_id搜索标签")
    public Response<MysqlTags> getTagById(@PathVariable int id){

        MysqlTags tag = mysqlTagsMapper.getTagById(id);
        return Response.success(tag);
    }

    /**
     * 根据标签类型搜索标签
     * 这里的参数 MysqlTagsType mysqlTagsType, 里面的 type_id 必须填写
     * 查询的是 tags 表的 tag_level_second
     * @param mysqlTagsType
     * @return
     */
    @RequestMapping(value = "/tag-manager/tags",
            method = RequestMethod.POST)
    @ApiOperation(value = "根据标签类型搜索标签",notes = "根据标签类型搜索标签")
    public Response<List<MysqlTags>> getTagsByType(
            @RequestBody MysqlTagsType mysqlTagsType){
        List<MysqlTags> tag = mysqlTagsMapper.getTagsByType(mysqlTagsType);
        return Response.success(tag);
    }

    /**
     * 获取标签第一层级分组
     * @param
     * @return
     */
    @RequestMapping(value = "/tag-manager/levels",
            method = RequestMethod.GET)
    @ApiOperation(value = "获取标签第一层级分组",notes = "获取标签第一层级分组")
    public Response<List<MysqlTagsType>> getTagsLevel(){

        List<MysqlTagsType> tag = mysqlTagsMapper.getTagsLevel();
        return Response.success(tag);
    }

    /**
     * 获取标签第二层级分组
     * @param
     * @return
     */
    @RequestMapping(value = "/tag-manager/levels/{id}",
            method = RequestMethod.GET)
    @ApiOperation(value = "获取标签第二层级分组",notes = "获取标签第二层级分组")
    public Response<List<MysqlTagsType>> getTagsSecLevel(@PathVariable int id){

        List<MysqlTagsType> tag = mysqlTagsMapper.getTagsSecLevel(id);
        return Response.success(tag);
    }


    /**
     * 添加标签基础信息
     * @param tag
     * @return
     */
    @RequestMapping(value = "/tag-manager/tag",
            method = RequestMethod.POST)
    @ApiOperation(value = "添加标签基础信息",notes = "添加标签基础信息")
    public Response<MysqlTags> addTag(@RequestBody MysqlTags tag){

        Integer tagId = mysqlTagsMapper.addTag(tag);
        //取得新添加的标签的自增id
        Integer i = tag.getTag_id();
        MysqlTags addTag = mysqlTagsMapper.getTagById(i);
        return Response.success(addTag);
    }


    /**
     * 编辑标签基础信息
     * 这里的参数 MysqlTags tag, 里面的 tag_id 和 tag_name必须填写
     * 是以修改 tag_name 作为例子
     * @param tag
     * @return
     */
    @RequestMapping(value = "/tag-manager/tag",
            method = RequestMethod.PUT,
            consumes="application/json;charset=utf-8")
    @ApiOperation(value = "编辑标签基础信息",notes = "编辑标签基础信息")
    public Response<MysqlTags> modifyTag(
            @RequestBody MysqlTags tag){

        Boolean success = mysqlTagsMapper.modifyTag(tag);
        if(success) {
            MysqlTags modifyTag = mysqlTagsMapper.getTagById(tag.getTag_id());
            return Response.success(modifyTag);
        }else {
            return Response.FAIL(ResultCode.FAIL);
        }
    }


    /**
     * 删除标签
     * @return
     */
    @RequestMapping(value = "/tag-manager/tag/{id}",
            method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标签",notes = "删除标签")
    public Response deleteTag(@PathVariable int id){

        Boolean delete = mysqlTagsMapper.deleteTagById(id);
        if(delete) {
            return Response.success(new HashMap<>());
        }else{
            return Response.FAIL(ResultCode.FAIL);
        }
    }


}
