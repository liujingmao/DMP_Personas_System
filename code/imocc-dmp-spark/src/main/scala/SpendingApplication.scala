import config.ImoccSparkConf.spark
import config.ImoccSparkConf.properties
import Tags.Spending
import Utils.SparkUtils

/**
 * 基于 K-Means 的消费等级划分
 */
object SpendingApplication {
  //RFM表的Schema
  case class RFM(user_id:String,
                 r:Double,
                 f:Double,
                 m:Double)
  def main(args: Array[String]): Unit = {

    /**
     * 指标 消费次数 消费总金额 最近一次消费时间
     */

    //集群运行 读取hive数仓RFM表
    //    val data = HiveUtil.hiveRead("select * from dw.dws_rfm")


    //读取RFM表
    val data = SparkUtils.getDataSetByCSV[RFM](
      properties.getProperty("rfm_file")
    )
    println("##########RFM表#################")
    data.show(false)

    //消费模型
    val spending = new Spending()
    spending.gradationByKMS(data)

    spark.stop()

  }
}
