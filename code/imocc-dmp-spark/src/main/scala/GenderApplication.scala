import Tags.Gender
import Utils.SparkUtils
import config.ImoccSparkConf.spark
import config.ImoccSparkConf.properties


/**
 * 基于朴素贝叶斯预测用户性别
 */
object GenderApplication {
  //订单详情表Schema
  case class OrderDetails(
                         user_id:String,
                         order_id:String,
                         //品类id
                         category_id:String,
                         price:Double,
                         //格式必须是 Y-m-d
                         finish_time:String,
                         finish_time_hour:Int
                         )
  //用户表Schema
  case class Users(
                    user_sk:Int,
                    user_id:String,
                    //用户等级
                    user_lv_cd: Int,
                  //性别：0男生 1女生
                   gender:Int,
                    age: Int)
  def main(args: Array[String]): Unit = {

    //集群运行 读取hive数仓订单明细事实表
    //val order_detail_data = HiveUtil.hiveRead("select * from dw.fact_order_details")
    //集群运行 读取hive数仓用户维度表
    //val users_data = HiveUtil.hiveRead("select * from dwd.dim_user")


    //本地运行 读取订单详情表
    val order_detail_data = SparkUtils.getDataSetByCSV[OrderDetails](
      properties.getProperty("order_detail_file"))
    println("#########订单详情表#########")
    order_detail_data.show(false)
    //本地运行 读取用户表
    val users_data = SparkUtils.getDataSetByCSV[Users](
      properties.getProperty("users_file")
    )
    println("#########用户表#########")
    users_data.show(false)

    //合并订单详情表和用户表
    val data = SparkUtils.join(users_data,order_detail_data,Seq("user_id"))
    println("#########合并订单详情表和用户表#########")
    data.show(false)

    //性别预测
    val gender =new Gender()
    gender.genderForecast(data)

    spark.stop()
  }
}
