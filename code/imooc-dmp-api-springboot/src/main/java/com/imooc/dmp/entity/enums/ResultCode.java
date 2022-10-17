package com.imooc.dmp.entity.enums;

import lombok.Getter;

/**
 * 接口返回状态码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(200,"操作成功"),
    FAIL(1001,"接口错误");

    private Integer code;
    private String msg;

    ResultCode(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }
}
