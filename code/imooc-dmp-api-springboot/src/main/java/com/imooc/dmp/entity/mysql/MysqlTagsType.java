package com.imooc.dmp.entity.mysql;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Program: dmp
 * Description: 标签层级实体类
 */
@Data
@ApiModel
public class MysqlTagsType {

    @Id
    @ApiModelProperty(value = "id")
    private Integer type_id;

    @ApiModelProperty(value = "标签类型名称")
    private String type_name;
    @ApiModelProperty(value = "父id")
    private Integer type_parent_id;
    @ApiModelProperty(value = "标签类型所属组")
    private String type_group;
}
