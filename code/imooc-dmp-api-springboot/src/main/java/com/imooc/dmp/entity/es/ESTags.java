package com.imooc.dmp.entity.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
//声明文档，索引名称要小写
//在es7以后，文档类型type被废除，所有的文档类型type只有_doc
@Document(indexName = "imooc",type = "tags")
public class ESTags {

    @Id
    //索引id(_index)是设置为自动生成的·
    //@Id将实体类的字段id和索引id(_index)进行映射
    private String id;
    @Field(type = FieldType.Keyword)
    //@Field将实体类的字段和mapping的字段进行映射
    private String user_id;
    @Field(type = FieldType.Text,analyzer = "imooc_tags")
    private String user_tags;
}
