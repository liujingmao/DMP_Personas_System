import Utils.SparkUtils
import ml.TFIDFModel
import config.ImoccSparkConf.properties

/**
 * 中文分词+TF-IDF
 */
object TFIDFApplication {
  //评论表的Schema
  case class Comments(user_id:String,
                      item_id:String,
                      content:String,
                      label:Double)
  def main(args: Array[String]): Unit = {

    //集群运行 读取hive数仓商品评论事实表
    //val comment_data = HiveUtil.hiveRead("select * from dw.fact_goods_comments")


    //本地运行 读取评论表
    val comment_data =
      SparkUtils.getDataSetByCSV[Comments](
        properties.getProperty("comments_file")
      )

    val data = SparkUtils.jieba(
        comment_data,
        "content",
        "jieba_words")

    println("#########Jieba切词################")
    data.show(false)
    data.printSchema()

    val tfidf = new TFIDFModel()
    tfidf.train(data,"jieba_words","features")

  }
}
