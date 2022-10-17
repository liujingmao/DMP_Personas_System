### ES 表结构

* 索引名：imooc
* 索引type: tags
* 索引id：自动生成
* 字段：user_id，user_tags 
* 分析器：imooc_tags（以“|”分词）

```
{
    "imooc": {
        "mappings": {
            "tags": {
                "properties": {
                    "user_id": {
                        "type": "keyword"
                    },
                    "user_tags": {
                        "type": "text",
                        "analyzer": "imooc_tags"
                    }
                }
            }
        }，
        "settings": {
            "index": {
                "number_of_shards": "5",
                "provided_name": "imooc",
                "creation_date": "1628174556677",
                "analysis": {
                    "analyzer": {
                        "imooc_tags": {
                            "type": "pattern",
                            "parttern": "|"
                        }
                    }
                },
                "number_of_replicas": "1",
                "uuid": "Q-6wzldqQ2qn2rP_VL7oeg",
                "version": {
                    "created": "5061699"
                }
            }
        }
    }
}
```