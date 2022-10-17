import config.ImoccSparkConf.spark
import config.ImoccSparkConf.properties
import Tags.RFM
import Utils.SparkUtils
import org.apache.spark.sql.functions._

/**
 * 基于RFM模型给客户打标签
 */
object RFMApplication {
  //订单表
  case class Orders(
                   order_sk: Int,
                   order_id: String,
                   user_id: String,
                   final_total_amount: Double,
                   finish_time:String,
                   finish_time_hour:String
                   )
  def main(args: Array[String]): Unit = {

    //集群运行 读取hive数仓订单维度表
    //val orders_data = HiveUtil.hiveRead("select * from dwd.dim_order")

    //本地运行 读取订单表
    val orders_data =
      SparkUtils.getDataSetByCSV[Orders](properties.getProperty("orders_file"))

    //添加截止时间列,计算最近一次消费的时间间隔
    val data = orders_data.withColumn("current_time",
      lit("2020-05-30"))

    println("#######订单数据集###########")
    data.show(false)

    //RFM模型
    val rfm = new RFM()
    //基于RFM模型的用户分群
    rfm.rfmGradation(data)


    spark.stop()

  }
}
