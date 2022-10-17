### ClickHouse表结构
1.
|表名|表解释|
| -- | -- |
|ch_tags_string_with_bitmap|标签值为String并且包含Bitmap对象的标签表

|字段名|字段类型|字段解释|
| -- | -- | -- |
|tag_id|UInt64|标签id|
|tag_value|String|标签值
|user_ids|Bitmap|用户id集合


2.
|表名|表解释|
| -- | -- |
|ch_tags_string_with_view|将ch_hive_tags_string_map_all 按标签做聚合

|字段名|字段类型|字段解释|
| -- | -- | -- |
|tag_id|UInt64|标签id
|tag_value|String|标签值
|user_ids|Array(UInt64)|用户id集合


3.
|表名|表解释|
| -- | -- |
|ch_hive_tags_string_map_all|从hive导入的标签表|

|字段名|字段类型|字段解释|
| -- | -- | -- |
|tag_id|UInt64|标签id
|tag_value|String|标签值
|user_id|UInt64|用户id
