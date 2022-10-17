package com.imooc.dmp.entity.hive;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 标签实体类
 */
@Data
public class HiveTags {

    @ApiModelProperty(value = "标签规则")
    private String tag_rule;
    @ApiModelProperty(value = "标签id (数值型)")
    private Integer tag_id;
    @ApiModelProperty(value = "标签一级层级")
    private Integer tag_level;
    @ApiModelProperty(value = "标签名称")
    private String tag_name;
    @ApiModelProperty(value = "标签二级层级")
    private Integer tag_level_second;
    @ApiModelProperty(value = "标签id (字符型)")
    private String tag_id_string;
}
