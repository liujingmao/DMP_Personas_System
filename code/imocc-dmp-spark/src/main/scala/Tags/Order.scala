package Tags

import Utils.{HiveUtil, TagUtils}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

/**
 * 订单模型
 */
class Order {

  /**
   * 用户消费行为
   * @param dataSet
   */
  def orderBehavior(dataSet:DataFrame): Unit ={

    /**
     * 重要消费行为：
     * 1.第一次消费时间 ，最后一次消费时间
     * 如果第一次消费时间和最后一次消费时间重合，这个用户就是流失用户
     * 2.最大消费金额
     * 如果最大消费金额发生在促销的日期，这个用户对促销敏感度高
     * 3.累计消费次数
     * 这个用户的复购行为(这里先忽略是否同一个商品品类)
     * 4.下单时间：早上，晚上，下午，凌晨
     * 比较年轻的用户会习惯于凌晨进行下单
     *
     */

     val order_portrait = dataSet.groupBy(col("user_id"))
      .agg(
        //第一次消费时间
        min(to_date(col("finish_time"))).as("first_order_time"),
        //最后一次消费时间
        max(to_date(col("finish_time"))).as("last_order_time"),
        //第一次消费时间距今的时间间隔
        datediff(
          max(to_date(col("current_time"))),
          min(to_date(col("finish_time")))
        ).as("first_order_diff"),
        //最后一次消费时间距今的时间间隔
        datediff(
          max(to_date(col("current_time"))),
          max(to_date(col("finish_time")))
        ).as("last_order_diff"),
        //最大消费金额
        max(col("final_total_amount")).as("max_amount"),
        //累计消费次数
        count(col("*")).as("total_order_count")
      )

    println("#########首次和最后消费时间,最大消费金额,累计消费次数################")
    order_portrait.show(false)

    /** 偏好下单时间段 */

    //上午下单时间段
    val forenoon_data = dataSet.where(col("finish_time_hour")>=8)
      .where(col("finish_time_hour")<=11)
      .groupBy(col("user_id"))
      .agg(count("*").as("forenoon_order_cnt"))

    println("##########上午下单时间段##########")
    forenoon_data.show(false)

    //下午下单时间段
    val afternoon_data = dataSet.where(col("finish_time_hour")>=12)
      .where(col("finish_time_hour")<=17)
      .groupBy(col("user_id"))
      .agg(count("*").as("afternoon_order_cnt"))

    println("##########下午下单时间段##########")
    afternoon_data.show(false)

    //晚上下单时间段
    val night_data = dataSet.where(col("finish_time_hour")>=18)
      .where(col("finish_time_hour")<=23)
      .groupBy(col("user_id"))
      .agg(count("*").as("night_order_cnt"))

    println("##########晚上下单时间段##########")
    night_data.show(false)

    //凌晨下单时间段
    val morning_data = dataSet.where(col("finish_time_hour")>=1)
      .where(col("finish_time_hour")<=7)
      .groupBy(col("user_id"))
      .agg(count("*").as("morning_order_cnt"))

    println("##########晚上下单时间段##########")
    morning_data.show(false)


    //打标签

    //第一次消费时间===最后一次消费时间 流失用户 1
    //最后一次消费时间时间间隔>30 流失用户 1
    //最后一次消费时间时间间隔<30 && 第一次消费时间=!=最后一次消费时间 复购用户 2
    //累计消费次数>3 复购用户 2

    val tag_name_cn = "用户忠诚度"

    val order_rule = when(
      col("first_order_time")===col("last_order_time"),
      1)
      .when(
        col("last_order_diff")>=20,
        1)
      .when(
        col("first_order_time")=!=col("last_order_time")
          &&
          col("last_order_diff")<20,
        2)
      .when(
        col("total_order_count")>=3,
        2)

    val _order_tag = order_portrait.withColumn("tag_rule",order_rule)

    val order_tag = TagUtils.taggingByRule(_order_tag,tag_name_cn)

    println("########用户忠诚度标签#############")
    val hive_order_tag = order_tag.select(col("user_id"),
      col("tag_name_cn").as("tag_id"),
      col("tag_name").as("tag_value"))
    hive_order_tag.show(false)

    //forenoon_order_cnt > 2  偏好上午下单 1
    //afternoon_order_cnt > 2  偏好下午下单 2
    //night_order_cnt > 2  偏好晚上下单 3
    //morning_order_cnt > 2  偏好凌晨下单 4
    // <=1 不确定偏好下单时间 0

    val tag_name_cn2 = "下单时间段"

    //偏好上午下单
    val forenoon_rule = when(
      col("forenoon_order_cnt")>=2,
      1)
      .when(
        col("forenoon_order_cnt")===1,
        0)

    val _forenoon_tag = forenoon_data.withColumn("tag_rule",forenoon_rule)

    val forenoon_tag = TagUtils.taggingByRule(_forenoon_tag,"下单时间段")

    println("########上午下单标签#############")
    val hive_forenoon_tag = forenoon_tag.select(col("user_id"),
      col("tag_name_cn").as("tag_id")
      ,col("tag_name").as("tag_value"))
    hive_forenoon_tag.show(false)

    //偏好下午下单
    val afternoon_rule = when(
      col("afternoon_order_cnt")>=2,
      2)
      .when(
        col("afternoon_order_cnt")===1,
        0)

    val _afternoon_tag = afternoon_data.withColumn("tag_rule",afternoon_rule)

    val afternoon_tag = TagUtils.taggingByRule(_afternoon_tag,tag_name_cn2)

    println("########下午下单标签#############")
    val hive_afternoon_tag = afternoon_tag.select(col("user_id"),
      col("tag_name_cn").as("tag_id"),
      col("tag_name").as("tag_value"))
    hive_afternoon_tag.show(false)


    //偏好晚上下单
    val night_rule = when(
      col("night_order_cnt")>=2,
      3)
      .when(
        col("night_order_cnt")===1,
        0)

    val _night_tag = night_data.withColumn("tag_rule",night_rule)

    val night_tag = TagUtils.taggingByRule(_night_tag,tag_name_cn2)

    println("########晚上下单标签#############")
    val hive_night_tag = night_tag.select(col("user_id"),
      col("tag_name_cn").as("tag_id"),
      col("tag_name").as("tag_value"))
    hive_night_tag.show(false)


    //偏好凌晨下单
    val morning_rule = when(
      col("morning_order_cnt")>=2,
      4)
      .when(
        col("morning_order_cnt")===1,
        0)

    val _morning_tag = morning_data.withColumn("tag_rule",morning_rule)

    val morning_tag = TagUtils.taggingByRule(_morning_tag,tag_name_cn2)

    println("########凌晨下单标签#############")
    val hive_morning_tag = morning_tag.select(col("user_id"),
      col("tag_name_cn").as("tag_id"),
      col("tag_name").as("tag_value"))
    hive_morning_tag.show(false)

    //保存到hive数仓的用户标签流水表

//    val hive_table = "dw.dws_user_action_tags_map_all"
//
//    println("########保存用户忠诚度标签#############")
//    HiveUtil.hiveSave(hive_order_tag,hive_table)
//    println("########保存上午下单标签#############")
//    HiveUtil.hiveSave(hive_forenoon_tag,hive_table)
//    println("########保存下午下单标签#############")
//    HiveUtil.hiveSave(hive_afternoon_tag,hive_table)
//    println("########保存晚上下单标签#############")
//    HiveUtil.hiveSave(hive_night_tag,hive_table)
//    println("########保存凌晨下单标签#############")
//    HiveUtil.hiveSave(hive_morning_tag,hive_table)

  }
}
