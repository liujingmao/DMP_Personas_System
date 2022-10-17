import config.ImoccSparkConf.{sc, spark}
import spark.implicits._
import Utils.{HiveUtil, SparkUtils, TagUtils}
import config.ImoccSparkConf.{properties, spark}
import ml.TFIDFModel
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.mllib.classification.SVMModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.{col, when}
import org.apache.spark.ml.linalg.{SparseVector => MLSparseVector}

/**
 * 使用训练好的SVM模型标注情感标签
 */
object SentimentActApplication {
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

    //读取已训练好的SVM模型
    val svm = SVMModel.load(sc,"hdfs://namenode:9000/model/SVM")
    //提取文本特征，用向量来表示文本，让计算机读懂文本
    //用tf-idf提取文本特征
    val tfidfModel = new TFIDFModel()
    val tfidf_data =
      tfidfModel.train(data,"filtered_words","features")
    val svm_data =
      tfidf_data.select(col("user_id"),col("features"))
    println("##############已提取的文本特征##################")
    svm_data.show(false)


    val mllib_svm_data = svm_data.rdd.map({
      case Row(user_id:String,features:MLSparseVector)
      =>{
        //数据集的向量转换
        val v = Vectors.fromML(features)
        //通过已训练好的SVM模型进行分类
        val predict_label = svm.predict(v)
        (user_id,predict_label)
      }
    })
    val predict_data = mllib_svm_data.toDF("user_id","prediction")
    //打标签
    val sentiment_rule = when(
      col("prediction")===0,
      0)
      .when(
        col("prediction")===1,
        1)
    val _sentiment_tag = predict_data.withColumn("tag_rule",sentiment_rule)
    val sentiment_tag = TagUtils.taggingByRule(_sentiment_tag,"物品喜好")

    println("########物品喜好标签#############")
    val hive_sentiment_tag = sentiment_tag.select(col("user_id"),
      col("tag_name_cn").as("tag_id")
      ,col("tag_name").as("tag_value"))
    hive_sentiment_tag.show(false)

    //保存到hive数仓的用户标签流水表
    val hive_table = "dw.dws_user_action_tags_map_all"

    println("########保存用户物品喜好标签#############")
    HiveUtil.hiveSave(hive_sentiment_tag,hive_table)

    spark.stop()

  }
}
