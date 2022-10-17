##### 将整个spark_jar文件夹复制到spark-master容器

```
docker cp spark_jar spark-master:/
```

```
docker exec -it spark-master bash
```

```
cd spark_jar
```

##### 执行脚本，验证项目功能

* 将hive标签表导入到ES

```
注意要启动hbase,es,hive
sh TagsToESApplication.sh
```

* 通过订单挖掘用户的行为属性

```
sh OrdersApplication.sh
```

* 基于TF-IDF和SVM对商品评论的情感提取

```
要先把停用词上传到hdfs
docker cp stoplist.txt namenode:/
docker exec -it namenode bash
hdfs dfs -put stoplist.txt /
```

```
sh SentimentApplication.sh
```

* 计算用户行为标签权重和行为偏好

```
sh TagsWeightApplication.sh
```