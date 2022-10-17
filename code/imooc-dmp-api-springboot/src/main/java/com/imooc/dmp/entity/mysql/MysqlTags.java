package com.imooc.dmp.entity.mysql;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * 标签实体类
 */
@Data
@ApiModel
public class MysqlTags {

    @Id
    @ApiModelProperty(value = "id")
    private Integer tag_id;

    @ApiModelProperty(value = "标签一级分组id")
    private Integer tag_level;
    @ApiModelProperty(value = "标签值")
    private String tag_name;
    @ApiModelProperty(value = "标签二级分组id")
    private Integer tag_level_second;
    @ApiModelProperty(value = "标签id")
    private String tag_id_string;
    @ApiModelProperty(value = "标签名称")
    private String tag_name_cn;
    @ApiModelProperty(value = "标签一级分组名称")
    private String tag_level_cn;
    @ApiModelProperty(value = "标签二级分组名称")
    private String tag_level_second_cn;
    @ApiModelProperty(value = "标签值类型cn")
    private String tag_value_type_cn;
    @ApiModelProperty(value = "标签值类型en")
    private String tag_value_type;
    @ApiModelProperty(value = "标签有效天数")
    private Integer tag_expire;
    @ApiModelProperty(value = "标签类型")
    private String tag_type_cn;
    @ApiModelProperty(value = "标签类型id")
    private Integer tag_type;
    @ApiModelProperty(value = "标签状态")
    private Integer tag_status;

}
