
import Utils.{HiveUtil, TagUtils}
import config.ImoccSparkConf.spark
import spark.implicits._
import ml.TFIDFModel
import org.apache.spark.sql.functions._

import scala.collection.mutable.ListBuffer

/**
 * 计算用户行为标签权重和行为偏好*
 */
object TagsWeightApplication {
  def main(args: Array[String]): Unit = {

    // string to int
    spark.udf.register("cast_map_string_to_int",
      (map:Map[String,String])=>{
        var tags_new:Map[Int,Int] = Map()
        for(kv <- map) {
          val k = kv._1.toInt
          val v = kv._2.toInt
          tags_new += (k -> v)
        }
        tags_new
      })

    //读取用户行为标签聚合统计表
    val tags_map_sql = """
          |select user_id,item_id,
          |cast_map_string_to_int(tags) as tags
          |from
          |dw.dws_user_action_tags_map_count_all
          |limit 1000
      """.stripMargin
    val _tags_map_data = HiveUtil.hiveRead(tags_map_sql)
    println("###########用户行为标签聚合统计表############")
    _tags_map_data.show(false)
    _tags_map_data.printSchema()


    //Spark对Hive的Map数据类型的读取
    val tags_map_data = _tags_map_data.rdd.map(x=>{
      val tags = x.getAs[Map[Int,Int]]("tags")
      tags
    }).collect



    val tags_map_data2 = _tags_map_data.rdd.map(x=>{
      val user_id = x.getAs[String]("user_id")
      val item_id = x.getAs[String]("item_id")
      (user_id,item_id)
    }).collect

    //构造Map[(用户id,商品id),Map[行为标签id,标注次数]]
    //将所有用户被标注的标签放到Map里
    val all_user_tags_data = tags_map_data2.zip(tags_map_data).toMap

    //读取hive的字段
    val tags_map_data_all = _tags_map_data.rdd.map(x=>{
      val user_id = x.getAs[String]("user_id")
      val item_id = x.getAs[String]("item_id")
      val tags = x.getAs[Map[Int,Int]]("tags")
      (user_id,item_id,tags)
    }).collect


    //读取行为维度表
    val tags_sql = """
      |select action_id
      |from
      |dwd.dim_user_action
      """.stripMargin
    val _tags_data = spark.sql(tags_sql)
    println("###########行为标签表############")
    _tags_data.show(false)
    val tags_data = _tags_data.rdd.map(x=>{
      val tag_id = x.getAs[Int]("action_id")
      tag_id
    }).collect()


    //通过TF-IDF计算行为标签权重
    val tfidfModel = new TFIDFModel()

    //ListBuffer[Tuple(用户id,商品id,标签id,权重值)]
    var list = ListBuffer[
      Tuple4[
        String,
        String,
        Int,
        Double
      ]]()

    //将用户行为标签聚合统计表每一行数据读取到TFIDF算法模型
    tags_map_data_all.foreach(line=> {

      val user_id = line._1
      val item_id = line._2
      val user_tags = line._3

      //返回格式：ListBuffer[Tuple(tfidf值,标签id,标签id被标注次数)]
      val tfidf_weight = tfidfModel.tfidf(
        user_tags,
        tags_data,
        all_user_tags_data,
        user_id,
        item_id
      )
      println(tfidf_weight)

      //时间衰减因子
//      val timeExp_weight = TagUtils.timeExp(
//        tfidf_weight.last._1,
//        year,month,day)

      val timeExp_weight = 0.3
      //计算没有行为权重的权重值
      //时间衰减因子*tfidf权重
      println("tfidf权重="+tfidf_weight.last._1)
      println("行为次数="+tfidf_weight.last._3)
      val noActionWeight_weight = tfidf_weight.last._1*tfidf_weight.last._3 * timeExp_weight

      //将结果放入到list
      val rs = (
        user_id,
        item_id,
        tfidf_weight.last._2, //行为标签id
        noActionWeight_weight
      )

      list += rs
      println(rs)

    })


    println("########TFIDF算法模型结果###########")
    for(i<-list){
      println(i)
    }

    //生成dataframe
    val _weight_dataFrame =
      spark.sparkContext
        .parallelize(list)
        .toDF("user_id","item_id","tag_id", "_weight")
    println("###########将TFIDF算法模型结果生成dataframe############")
    _weight_dataFrame.show(false)

    //读取用户行为维度表
    val dim_action_sql = """
                         |select action_weight,action_tag_id,action_id
                         |from
                         |dwd.dim_user_action
      """.stripMargin
    val dim_action_data = HiveUtil.hiveRead(dim_action_sql)
    println("###########用户行为维度表############")
    dim_action_data.show(false)

    //乘以用户行为维度表的行为权重，得到完整的标签权重值
    val get_weight = udf((_weight:Double,action_weight:Double)=>_weight*action_weight)
    //联合用户行为维度表，得到行为标签对应的行为权重
    val weight_data = _weight_dataFrame.join(dim_action_data,
      _weight_dataFrame.col("tag_id")
        ===
      dim_action_data.col("action_id"))
      //乘以行为权重
        .withColumn("tag_weight",
          get_weight(
            col("_weight"),
            col("action_weight")))
      //删掉不需要的列
        .drop(col("action_tag_id"))
        .drop(col("action_weight"))
        .drop(col("_weight"))
        .drop(col("tag_id"))

    println("###########完整权重值############")
    weight_data.show(false)


    //保存到用户行为标签权重主题表
    HiveUtil.hiveSave(weight_data,"dw.dwt_user_action_tags_weight")

    //根据标签权重值求topN，得到用户的行为偏好标签
    //读取用户行为标签权重主题表
    val tags_weight_sql = """
                           |select *
                           |from
                           |dw.dwt_user_action_tags_weight
      """.stripMargin
    val tags_weight_data = HiveUtil.hiveRead(tags_weight_sql)
    println("###########用户行为标签权重主题表############")
    tags_weight_data.show(false)

    //获取每个用户标签权重最高的行为标签对应的物品id，就是用户的偏好标签
    val tags_weight_top = tags_weight_data.groupBy(
      col("user_id"),col("item_id"))
        .agg(max(col("tag_weight")))
    println("###########每个用户标签权重最高的行为标签对应的物品id############")
    tags_weight_top.show(false)

    spark.stop()
  }
}
