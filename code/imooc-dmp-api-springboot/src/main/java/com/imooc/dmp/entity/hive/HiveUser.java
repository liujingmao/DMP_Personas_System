package com.imooc.dmp.entity.hive;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户维度表实体类
 */
@Data
@ApiModel
public class HiveUser {

    @ApiModelProperty(value = "代理键")
    private String user_sk;
    @ApiModelProperty(value = "用户id")
    private String user_id;
    @ApiModelProperty(value = "用户等级")
    private String user_lv_cd;
    @ApiModelProperty(value = "用户性别")
    private int gender;
    @ApiModelProperty(value = "用户年龄")
    private int age;
}
