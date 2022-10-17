package com.imooc.dmp.entity.mysql;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Program: dmp
 * Description: Mysql人群实体类
 */
@Data
@ApiModel
public class MysqlCrowds {

    @Id
    @ApiModelProperty(value = "id")
    private Integer crowd_id;

    @ApiModelProperty(value = "人群名称")
    private String crowd_name;
    @ApiModelProperty(value = "人群属性")
    private String crowd_type_cn;
    @ApiModelProperty(value = "人群数量")
    private Long crowd_count;
    @ApiModelProperty(value = "有效天数")
    private Integer expire;
    @ApiModelProperty(value = "人群所属组 第一层级")
    private String crowd_group;
    @ApiModelProperty(value = "人群所属组 第二层级")
    private String crowd_group_second;
    @ApiModelProperty(value = "ch表id")
    private Integer ch_id;
    @ApiModelProperty(value = "筛选条件(标签)")
    private String tags;
}
