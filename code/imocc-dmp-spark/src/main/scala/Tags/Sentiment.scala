package Tags

import ml.{SVMModel, TFIDFModel}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

/**
 * 情感模型
 */
class Sentiment {

  /**
   * 情感分析·
   * @param dataSet
   */
  def anlysis(dataSet:DataFrame): Unit ={

    //提取文本特征，用向量来表示文本，让计算机读懂文本
    //用tf-idf提取文本特征
    val tfidfModel = new TFIDFModel()
    val tfidf_data =
      tfidfModel.train(dataSet,"filtered_words","features")

    val data =
      tfidf_data.select(col("label"),col("features"))

    println("#########选取label列和features列###############")
    data.show(false)

    //使用支持向量机(svm)算法,训练分类模型
    val svmModel = new SVMModel()
    val prediction = svmModel.train(data)


  }

}
