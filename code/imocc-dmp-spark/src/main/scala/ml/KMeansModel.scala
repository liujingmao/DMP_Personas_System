package ml

import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.sql.DataFrame

import scala.collection.mutable.ListBuffer

/**
 * K-Means 模型
 */
class KMeansModel {

  /**
   * K-Means 分群
   * @param dataSet 数据集
   * @param k   K值
   * @return
   */
  def kmsCluster(dataSet:DataFrame,k:Int): DataFrame ={

    //模型建立

    val kMeans = new KMeans()
      .setFeaturesCol("features")
      .setPredictionCol("prediction")
      .setK(k)
      .setMaxIter(10)
      .setSeed(100L)  //随机种子数

    val kmeans_model = kMeans.fit(dataSet)

    //计算每个用户的特征向量到中心点的欧式距离
    val prediction = kmeans_model.transform(dataSet)


    //保存k-means模型到hdfs
    //kmeans_model.save("hdfs://namenode:9000/model/KMeans")

    prediction


  }

  /**
   * 使用手肘法确定K值
   * @param dataSet  数据集
   * @param startK   初始K值
   * @param stopK    结束K值
   */
  def setK(dataSet:DataFrame,
           startK:Int,
           stopK:Int): Unit ={

    /**
     * 当K不断增大时，误差平方和(SSE)不断的减少
     * 当K小于真实的聚类数时，SSE的下降幅度是非常大的
     * 当K等于真实的聚类数时，SSE的下降幅度会骤减，K再继续增大时，SSE下降幅度趋于平缓
     * K和SSE的形状像手肘，拐弯最厉害的就是最佳的K值·
     *
     */

    val buffer = new ListBuffer[Double]()

    for(k<-startK to stopK){

      val kMeans = new KMeans()
        .setFeaturesCol("features")
        .setPredictionCol("prediction")
        .setK(k)
        .setMaxIter(10)
        .setSeed(100L)

      val kmeasModel = kMeans.fit(dataSet)

      //computeCost 在Spark3.0 会去掉
      val SSE = kmeasModel.computeCost(dataSet)
      println("###################")
      println("K="+k)
      println("SSE="+SSE)

      buffer.append(SSE)
    }


  }

}
