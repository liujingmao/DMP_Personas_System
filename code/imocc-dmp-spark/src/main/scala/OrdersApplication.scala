import Tags.Order
import Utils.{HiveUtil, SparkUtils}
import config.ImoccSparkConf.spark
import config.ImoccSparkConf.properties
import org.apache.spark.sql.functions.lit

/**
 * 通过订单挖掘用户的行为属性
 */
object OrdersApplication {

  //订单表Schema
  case class Orders(
                   order_sk: Int,
                   order_id: String,
                   user_id: String,
                   final_total_amount: Double,
                   //注意 finish_time的格式是Y-m-d
                   finish_time: String,
                   finish_time_hour: Int
                   )
  def main(args: Array[String]): Unit = {

    //集群运行 读取hive数仓订单维度表
//    val order_data = HiveUtil.hiveRead("select * from dwd.dim_order")

    //本地运行 读取订单表
    val order_data =
      SparkUtils.getDataSetByCSV[Orders](
        properties.getProperty("orders_file")
      )

    //添加截止时间列
    val data =
      order_data.withColumn("current_time",lit("2020-05-30"))

    println("#######订单表数据集#######")
    data.show(false)

    //订单模型
    val order = new Order()
    order.orderBehavior(data)

    spark.stop()
  }
}
