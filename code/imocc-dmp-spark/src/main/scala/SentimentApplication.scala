import Tags.Sentiment
import Utils.SparkUtils
import config.ImoccSparkConf.spark
import config.ImoccSparkConf.properties
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.sql.functions._

/**
 * 基于TF-IDF和SVM对商品评论的情感提取
 * (只训练模型，没有实现打标签)
 */
object SentimentApplication {
  //评论表Schema
  case class Comments(
                     user_id: String,
                     item_id: String,
                     content: String,
                     //类别：1对商品和服务满意 0对商品和服务不满意
                     label: Double
                     )
  def main(args: Array[String]): Unit = {

    //集群运行 读取hive数仓 商品评论事实表
    //val comment_data = HiveUtil.hiveRead("select * from dw.fact_goods_comments")


    //本地运行 读取评论表
    val comment_data =
      SparkUtils.getDataSetByCSV[Comments](
        properties.getProperty("comments_file")
      )
    println("#######评论表数据集#########")
    comment_data.show(false)

    //结巴分词
    val jieba_data =
      SparkUtils.jieba(comment_data,"content","jieba_words")
    println("#######评论表结巴分词#########")
    jieba_data.show(false)

    //集群运行 读取hdfs停用词表
//    val stop_words =
//      spark.sparkContext.textFile("hdfs://namenode:9000/stoplist.txt").collect()

    //本地读取停用词表
    val stop_words =
      spark.sparkContext.textFile("dataSets/stoplist.txt").collect()


    //去掉停用词
    val remover = new StopWordsRemover()
        .setStopWords(stop_words)
        .setInputCol("jieba_words")
        .setOutputCol("filtered_words")

    val data = remover.transform(jieba_data)
    println("#######去掉停用词#########")
    data.select(col("jieba_words"),col("filtered_words"))
      .show(false)

    //情感模型
    val sentiment = new Sentiment()
    //情感分析
    sentiment.anlysis(data)

    spark.stop()
  }
}
