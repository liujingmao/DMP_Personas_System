package Tags

import Utils.TagUtils
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

/**
 * RFM 客户价值模型
 */
class RFM {
  /**
   * 基于RFM模型的客户分群
   * @param dataSet 数据集
   * @return 带有RFM打分和标签的数据集
   */
  def rfmGradation(dataSet:DataFrame):DataFrame={

    //一般情况下，做RFM模型，使用的是过去一年的订单数

    /**  计算RFM的值 */

    //计算R: 每个用户最近一次消费距今的时间间隔
    //datediff: 计算两个日期的间隔
    //to_date : String 转 date

    val r_agg = datediff(
      max(to_date(col("current_time"))),
      max(to_date(col("finish_time"))) //获取finish_time的最大值
    ).as("r")

    //计算F: 每个用户在指定时间范围内的消费次数
    val f_agg = count(col("*")).as("f")

    //计算M：每个用户在指定时间范围内的消费总额

    val m_agg = sum(col("final_total_amount")).as("m")

    //按用户id聚合获取RFM
    val rfm_data =
      dataSet.groupBy(col("user_id")).agg(r_agg,f_agg,m_agg)

    println("########计算RFM#############")
    rfm_data.show(false)

    /** 给RFM打分 (5分制) */

    //R_SCORE: 1-3天=5分，4-6天=4分，7-9天=3分，10-15天=2分，大于16天=1分
    val r_score = when(col("r")>=1&&col("r")<=3,5)
        .when(col("r")>=4&&col("r")<=6,4)
        .when(col("r")>=7&&col("r")<=9,3)
        .when(col("r")>=10&&col("r")<=15,2)
        .when(col("r")>=16,1)
        .as("r_score")

    //F_SCORE: ≥100次=5分，80-99次=4分，50-79次=3分，20-49次=2分，小于20次=1分
    val f_score = when(col("f")>=100,5)
      .when(col("f")>=80 && col("f")<=99,4)
      .when(col("f")>=50 && col("f")<=79,3)
      .when(col("f")>=20 && col("f")<=49,2)
      .when(col("f")<20,1)
      .as("f_score")

    //M_SCORE: ≥10000=5分，8000-10000=4分，5000-8000=3分，2000-5000=2分，<2000=1分
    val m_score = when(col("m")>=10000,5)
      .when(col("m")>=8000 && col("m")<10000,4)
      .when(col("m")>=5000 && col("m")<8000,3)
      .when(col("m")>=2000 && col("m")<5000,2)
      .when(col("m")<2000,1)
      .as("m_score")

    val rfm_score = rfm_data.withColumn("r_score",r_score)
        .withColumn("f_score",f_score)
        .withColumn("m_score",m_score)

    println("###########R,F,M打分####################")
    rfm_score.show(false)

    //计算RFM的总分
    val _rfm_w_score = (col("r_score")*0.6
      +col("f_score")*0.3
      +col("m_score")*0.1
      ).as("rfm_score")

    val rfm_w_score = rfm_score.withColumn("rfm_score",_rfm_w_score)

    println("##########RFM的总分值###############")
    rfm_w_score.orderBy(desc("rfm_score")).show(false)

    val hive_rfm_w_score = rfm_w_score.select(
      col("user_id"),
      col("r"),
      col("f"),
      col("m")
    )
    println("##########准备写入到Hive数仓rfm表###############")
    hive_rfm_w_score.show(false)
    //写入到Hive数仓rfm表

    //    val hive_table = "dw.dws_rfm"
    //
    //    println("########写入到Hive数仓rfm表#############")
    //    HiveUtil.hiveSave(rfm_w_score,hive_table)


    /** 打标签 */

    //重要价值用户  1
    //重要保持用户  2
    //一般价值用户  3

    //RFM_SCORE >= 9: 重要价值用户
    //RFM_SCORE >= 5 && RFM_SCORE < 9: 重要保持用户
    //RFM_SCORE < 5: 一般价值用户


    val tag_rule = when(col("rfm_score")>=9,1)
        .when(col("rfm_score")>=5&&col("rfm_score")<9
          ,2)
    .when(col("rfm_score")<5,3)

    val _rfm_tag = rfm_w_score.withColumn("tag_rule",tag_rule)

    val rfm_tag = TagUtils.taggingByRule(_rfm_tag,"rfm客户模型")

    println("########RFM客户价值标签#############")
    val hive_rfm_tag = rfm_tag.select(col("user_id"),
      col("tag_name_cn").as("tag_id"),
      col("tag_name").as("tag_value"))
    hive_rfm_tag.show(false)

    //保存到hive数仓的用户标签流水表

    //    val hive_table = "dw.dws_user_action_tags_map_all"
    //
    //    println("########保存RFM客户价值标签#############")
    //    HiveUtil.hiveSave(hive_rfm_tag,hive_table)

    dataSet

  }

}
