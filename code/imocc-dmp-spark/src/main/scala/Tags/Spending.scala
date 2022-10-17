package Tags

import Utils.TagUtils
import ml.KMeansModel
import org.apache.spark.ml.feature.{StandardScaler, VectorAssembler}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

/**
 * 客户消费模型
 */
class Spending {

  /**
   * 基于K-Means的用户消费等级划分
   * @param dataSet
   */
  def gradationByKMS(dataSet:DataFrame): Unit = {
     /** 将数据转化特征向量 */

    //将RFM合并为单个特征列
    val rfm_vectorAss = new VectorAssembler()
      .setInputCols(Array("r","f","m"))
      .setOutputCol("assembler_features")
      .transform(dataSet)

    //特征标准化，将标准差缩放到1

    val standardScaler = new StandardScaler()
      .setInputCol("assembler_features")
      .setOutputCol("features")
      .setWithMean(false) //是否生成稠密向量

    val scalerModel = standardScaler.fit(rfm_vectorAss)
    val rfm_scaler = scalerModel.transform(rfm_vectorAss)

    println("#########RFM特征处理###############")
    rfm_scaler.show(false)
    rfm_scaler.printSchema()

    /** 通过K-Means模型对用户进行分群*/
    val kms = new KMeansModel()
    //kms.setK(rfm_scaler,2,11)
    val prediction = kms.kmsCluster(rfm_scaler,4)
    println("#############")
    prediction.show(false)

    val spendingCluster = prediction.groupBy(col("user_id"),col("prediction"))
      .agg(
        avg("r").as("avg_r"),
        avg("f").as("avg_f"),
        avg("m").as("avg_m")
      )

    println("##########K-Means用户分群###############")
    spendingCluster.show(false)
    /** 打标签 */

    //高消费用户      1
    //普通消费用户    2
    //低消费用户      3

    //avg("m") > 400 高消费用户
    //avg("m") < 400 && avg("m") > 100 普通消费用户
    //avg("m") < 100 低消费用户

    val tag_rule = when(col("avg_m")>=400,1)
      .when(col("avg_m")>=100&&col("avg_m")<400
        ,2)
      .when(col("avg_m")<100,3)

    val _spending_tag = spendingCluster.withColumn("tag_rule",tag_rule)

    val spending_tag = TagUtils.taggingByRule(_spending_tag,"消费水平")

    println("########用户消费等级标签#############")
    val hive_spending_tag = spending_tag.select(col("user_id"),
      col("tag_name_cn").as("tag_id"),
      col("tag_name").as("tag_value"))
    hive_spending_tag.show(false)

    //保存到hive数仓的用户标签流水表
    //    val hive_table = "dw.dws_user_action_tags_map_all"
    //
    //    println("########保存用户消费等级标签#############")
    //    HiveUtil.hiveSave(hive_spending_tag,hive_table)
  }

}
