package com.imooc.dmp.utils;

import com.imooc.dmp.entity.enums.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 接口返回工具类
 */
@Data
public class Response<T> {

    //状态码
    @ApiModelProperty(value = "状态码")
    private Integer code;
    //返回数据
    @ApiModelProperty(value = "返回数据")
    private T data;
    //状态信息
    @ApiModelProperty(value = "返回信息")
    private String msg;

    public Response(){

    }


    /**
     * 成功的静态方法
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Response<T> success(T data){

        return fill(data,ResultCode.SUCCESS);
    }


    /**
     * 错误的静态方法
     * @param fail
     * @return
     */
    public static Response FAIL(ResultCode fail){

        return fill(null,fail);
    }

    /**
     * 接口返回值填充
     * @param data
     * @param resultCode
     * @param <T>
     * @return
     */
    public static <T> Response<T> fill(T data,ResultCode resultCode) {
        Response response = new Response();
        response.setCode(resultCode.getCode());
        response.setData(data);
        response.setMsg(resultCode.getMsg());
        return response;
    }
}
