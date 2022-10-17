import Utils.SparkUtils
import config.ImoccSparkConf.spark
import config.ImoccSparkConf.properties
import ml.XgboostModel
import spark.implicits._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.functions._
import org.apache.spark.ml.linalg.Vector


/**
 * 基于Xgboost的特征筛选
 */
object XgboostApplication {
  //用户行为-用户特征表Schema
  case class ActionsUser(
                        user_sk:Int,
                        user_id: String,
                        //用户等级
                        user_lv_cd: Int,
                        //性别 0男 1女
                        gender: Int,
                        age: Int
                        )
  //用户行为-物品特征表Schema
  case class ActionsProduct(
                           item_sk:Int,
                           item_id:String,
                           //物品属性a1
                           a1: Int,
                           //物品属性a2
                           a2: Int,
                           //物品属性a3
                           a3: Int,
                           //品类
                           cate: Int,
                           //品牌
                           brand: Int
                           )
  ////用户行为表Schema
  case class Actions(
                    user_id: String,
                    item_id: String,
                    time: String,
  //1.浏览（指浏览商品详情页）； 2.加入购物车；3.购物车删除；4.下单；5.关注；6.点击
                    action_type: Int,
                    cate: Int,
                    brand: Int
                    )
  case class Feature(
                    features:Vector,
                    label:Int
                    )
  def main(args: Array[String]): Unit = {

    /**
     * 特征维度：
     * 用户等级
     * 用户年龄
     * 用户性别
     * 物品属性a1
     * 物品属性a2
     * 物品属性a3
     * 品类
     * 品牌
     *
     * 类别：
     * 行为类型(浏览（指浏览商品详情页）加入购物车；购物车删除；下单；关注；点击)
     *
     * 样本类别：
     * 购买的作为正样本   1
     * 没有购买但有交互的作为负样本  0
     *
     */

      //在win执行要把 xgboost4j-0.90-criteo-20190702_2.11-win64.jar 导入

    //集群运行 读取hive数仓用户维度表
    //val action_user = HiveUtil.hiveRead("select * from dwd.dim_user")
    //集群运行 读取hive数仓商品维度表
    //val action_product = HiveUtil.hiveRead("select * from dwd.dim_product")
    //集群运行 读取hive数仓用户行为日志表
    //val action = HiveUtil.hiveRead("select * from dw.fact_user_actions")


    //本地运行 读取用户属性表
    val action_user = SparkUtils.getDataSetByCSV[ActionsUser](
      properties.getProperty("users_file"))
    println("########行为日志用户属性表##########")
    action_user.show(false)
    //本地运行 读取商品属性表
    val action_product = SparkUtils.getDataSetByCSV[ActionsProduct](
      properties.getProperty("products_file"))
    println("########行为日志商品属性表##########")
    action_product.show(false)
    //本地运行 读取行为日志表
    val action = SparkUtils.getDataSetByCSV[Actions](
      properties.getProperty("user_actions_file"))
    println("########行为日志表##########")
    action.show(false)


    //合并行为日志用户属性表，行为日志商品属性表，行为日志表
    val _data = SparkUtils.join(action,action_user,Seq("user_id"))
    val __data =
      SparkUtils.join(_data,action_product,Seq("item_id","cate","brand"))
    println("########合并行为日志用户属性表，行为日志商品属性表，行为日志表##########")
    __data.show(false)

    //处理null
    val data = __data.na.fill(0,Array("item_sk","a1","a2","a3"))
    println("########处理null##########")
    data.show(false)

    //查询是否还有null，如果还有null，xgboost会报错
    println("########查询是否还有null##########")
    println("总条数："+data.count())
    val no_null = data.na.drop()
    println("去掉空值的条数："+no_null.count())

    //添加label
    val label_fun = when(col("action_type")===4,1)
        .otherwise(0)
    val label_data = data.withColumn("label",label_fun)
    println("########添加label##########")
    label_data.show(false)


    //合并特征
    val assembler_data = new VectorAssembler()
        .setInputCols(Array(
          "gender",
          "age",
          "user_lv_cd",
          "a1",
          "a2",
          "a3",
          "cate",
          "brand"
        ))
        .setOutputCol("assembler_feature")
        .transform(label_data)
        .select(col("assembler_feature"),col("label"))

    println("########合并特征##########")
    assembler_data.show(false)

    //处理稀疏向量
    val vector_data = assembler_data.rdd.map(x=>{
      //读取Row
      //转化稠密向量
      val v = x.getAs[Vector]("assembler_feature").toDense
      val l = x.getAs[Int]("label")
      Feature(v,l)
    }).toDF()
    println("########处理稀疏向量##########")
    vector_data.show(false)

    //xgboost模型训练
    val xgboostModel = new XgboostModel()
    xgboostModel.train(vector_data,"features","label")

    spark.stop()
  }
}
