import Utils.{HiveUtil}
import config.ImoccSparkConf.spark
import org.elasticsearch.spark.rdd.EsSpark
import org.json4s.{DefaultFormats, Extraction}
import org.json4s.jackson.JsonMethods._


/**
 * 将hive标签表导入到ES
 */
object TagsToESApplication {
  def main(args: Array[String]): Unit = {

    //读取用户标签主题表
    val tags_sql = """
      |select *
      |from
      |dw.dwt_user_tags_map_all
      """.stripMargin
    //用户标签主题表是hbase的映射表
    //需要导入hive-hbase-handler依赖·

    val tags_data = HiveUtil.hiveRead(tags_sql)
    println("########用户标签主题表###########")
    tags_data.show(false)

    /**
     * Spark 写入ES的方式：
     * 1. 以Map的形式写入ES
     * 2. 以case class的形式写入ES
     * 3. 以json的形式写入ES
     */
    val tags_map_data = tags_data.rdd.map(x=>{
      //读取Row
      val user_id = x.getAs[String]("user_id")
      val tags = x.getAs[Map[String,String]]("user_tags")

      /**
       * ES存储标签索引的mapping:
       * user_id.type : keyword
       * user_tags.type : text (标签名_1:标签value值_1|标签名_2:标签value值_2)
       *
       */

        //map转json
       val json = compact(render(Extraction.decompose(tags)(DefaultFormats)))

      //去掉 ""{} , 将,转为|
      val tags_str = json.replace("{","")
        .replace("}","")
        .replace("\"","")
        .replace(",","|")

      Map("user_id"->user_id,"user_tags"->tags_str)

    })

    println("########用户标签主题表转RDD[Map]###########")
    tags_map_data.collect().foreach(println(_))

    //写入ES
    //通过 ES Api

    /**
     * saveToEs方法有3个入参：
     * 1. rdd
     * 2. 资源路径
     * 3. 索引_id
     */
    EsSpark.saveToEs(
      tags_map_data,
      "imooc/tags"
    )

    println("########ES写入成功###########")

    spark.stop()
  }
}
