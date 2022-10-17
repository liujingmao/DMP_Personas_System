### Hive表结构

#### 数据库

1. dwd
2. dw

#### 维度表

|表名|表解释|
| -- | -- |
|dwd.dim_product|商品维度表

|字段名|字段类型|字段解释|
| -- | -- | -- |
|item_sk|int|代理键
|item_id||商品id
|cate||商品分类
|brand||商品品牌
|a1||商品属性1
|a2||商品属性2
|a3||商品属性3

2. 
|表名|表解释|
| -- | -- |
|dwd.dim_user|用户维度表

|字段名|字段类型|字段解释|
| -- | -- | -- |
|user_sk||代理键
|user_id||用户id
|user_lv_cd||用户等级
|gender||用户性别
|age||用户年龄

3.
|表名|表解释|
| -- | -- |
|dwd.dim_order|订单维度表

|字段名|字段类型|字段解释|
| -- | -- | -- |
|order_sk||代理键
|order_id||订单id
|user_id||用户id
|final_total_amount||总金额
|finish_time||完成时间
|finish_time_hour||完成时间(小时)


4.
|表名|表解释|
| -- | -- |
|dwd.dim_tags|标签维度表

|字段名|字段类型|字段解释|
| -- | -- | -- |
|tag_rule||标签规则
|tag_id||标签id (数值型)
|tag_level||标签一级层级
(1用户信息类型 2消费信息类型 3行为信息类型)
|tag_name||标签值
|tag_level_second||标签二级层级
(用户信息类型: 1基本属性 2社会属性)
(行为信息类型：1浏览 2收藏 3购买 4搜索)
|tag_id_string||标签id (字符型)
|tag_name_cn||标签中文名称


5.
|表名|表解释|
| -- | -- |
|dwd.dim_user_action|用户行为维度表

|字段名|字段类型|字段解释|
| -- | -- | -- |
|action_id||用户行为id
|action_name||用户行为名称
|action_weight||用户行为权重
|action_tag_id||用户行为对应标签id


#### 事实表

1.
|表名|表解释|
| -- | -- |
|dw.fact_order_detail|订单明细事实表 

|字段名|字段类型|字段解释|
| -- | -- | -- |
|user_id||用户id
|item_id||商品id
|category_id||品类id
|price||总金额
|finish_time||完成时间
|finish_time_hour||完成时间(小时)


2.
|表名|表解释|
| -- | -- |
|dw.fact_goods_comments|商品评论事实表  

|字段名|字段类型|字段解释|
| -- | -- | -- |
|user_id||用户id
|item_id||商品id
|content||商品评论
|label||评论情感标签 （0：负评 1：正评）


3.
|表名|表解释|
| -- | -- |
|dw.fact_user_actions|用户行为日志表

|字段名|字段类型|字段解释|
| -- | -- | -- |
|user_id||用户id
|item_id||商品id
|time||发生时间
|type||用户行为类型
|cate||用户行为作用于的商品类别
|brand||用户行为作用于的商品品牌


#### 汇总表

1.
|表名|表解释|
| -- | -- |
|dw.dws_user_action_tags_map_all|用户标签流水表

|字段名|字段类型|字段解释|
| -- | -- | -- |
|user_id||用户id
|tag_id||标签名
|tag_value||标签value值


2.
|表名|表解释|备注
| -- | -- | -- |
|dw.dws_rfm|RFM表|数据由Spark写入|

|字段名|字段类型|字段解释|
| -- | -- | -- |
|user_id||用户id
|r||最近一次消费距今时间间隔
|f||消费次数
|m||消费金额


3.
|表名|表解释|
| -- | -- |
|dw.dws_user_action_tags_map_count_all|用户行为标签聚合统计表

|字段名|字段类型|字段解释|
| -- | -- | -- |
|user_id||用户id
|item_id||商品id
|tags||用户行为标签聚合统计 Map<标签id,被标注次数>
|year||年
|month||月
|day||日


4.
|表名|表解释|备注
| -- | -- | -- |
|dw.dwt_user_action_tags_weight|用户行为标签权重主题表|数据由Spark写入

|字段名|字段类型|字段解释|
| -- | -- | -- |
user_id：用户id
item_id: 商品id
tag_id：标签id
tag_weight：标签权重
year: 年
month: 月
day： 日

5.
|表名|表解释|备注|
| -- | -- | -- |
|dw.dwt_user_tags_map_all|用户标签主题表|这张表由 dw.dws_user_action_tags_map_all这张表写入|

|字段名|字段类型|字段解释|
| -- | -- | -- |
user_id: 用户id
user_tags: 用户标签聚合 Map<标签名,标签value值>

