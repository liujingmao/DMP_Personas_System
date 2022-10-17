package com.imooc.dmp.controller.mysql;

import com.imooc.dmp.entity.mysql.MysqlCrowds;
import com.imooc.dmp.mapper.mysql.MysqlCrowdsMapper;
import com.imooc.dmp.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * *******************************
 * Program: dmp
 * Description: Mysql人群管理
 * ******************************
 */
//允许跨域
@CrossOrigin
@RestController
@Api(value = "Mysql-Crowd-API", tags = "Mysql人群管理")
public class MysqlCrowdManagerController {

    @Autowired
    private MysqlCrowdsMapper mysqlCrowdsMapper;

    /**
     * 查询人群列表(分页)
     * @return
     */
    @RequestMapping(value = "/crowd-manager/crowds/{page}",
            method = RequestMethod.GET)
    @ApiOperation(value = "查询人群列表(分页)",notes = "查询人群列表(分页)")
    public Response<List<MysqlCrowds>> getCrowdsByPage(@PathVariable("page") Integer page){
        Map<String, Object> params = new HashMap<String, Object>();
        int size = 5;
        params.put("page",(page-1)*size);
        params.put("size",size);
        List<MysqlCrowds> crowds = mysqlCrowdsMapper.getCrowdsByPage(params);
        return Response.success(crowds);
    }

    /**
     * 生成人群包
     * @return
     */
    @RequestMapping(value = "/crowd-manager/crowds",
            method = RequestMethod.POST)
    @ApiOperation(value = "生成人群包",notes = "生成人群包")
    public Response<Map<String, Object>> downloadCrowds(){
        Map<String, Object> params = new HashMap<String, Object>();
        //生成excel人群表
        //TODO
        return Response.success(params);
    }
}
