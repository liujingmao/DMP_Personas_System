package ml

import config.ImoccSparkConf.{sc, spark}
import spark.implicits._
import Utils.SparkUtils
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.linalg.{SparseVector => MLSparseVector}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint

/**
 * 支持向量机算法模型
 */
class SVMModel {

  /**
   * 模型训练
   * @param dataSet
   */
  def train(dataSet:DataFrame): Unit ={

    //划分数据集
    val  Array(train,test) =
      SparkUtils.dataSplit(dataSet,0.8,0.2)

    //SVM只支持二分类·
    //使用mllib包里的SVM，这个SVM只实现了线性SVM，使用的随机梯度下降
    //SVMWithSGD对象里SGD就是指随机梯度下降
    //SVMWithSGD对象train方法需要的是LabelPoint的形式
    //SVMWithSGD对象使用的向量是mllib包里的向量

    //ml包的向量和mllib包里的向量之间的转换

    //训练集的向量转换
    val mllib_train = train.rdd.map({
      case Row(label:Double,features:MLSparseVector)
      =>{
        val v = Vectors.fromML(features)
        LabeledPoint(label,v)
      }
    })

    //测试·集的向量转换
    val mllib_test = test.rdd.map({
      case Row(label:Double,features:MLSparseVector)
      =>{
        val v = Vectors.fromML(features)
        v
      }
    })

    val numItor = 10
    //训练
    val svm_model = SVMWithSGD.train(mllib_train,numItor)
    //预测
    val prediction = svm_model.predict(mllib_test)
    println("############测试集##############")
    test.select(col("label")).show(false)
    println("#######预测##########")
    prediction.toDF("predictions").show(false)

    //保存svm模型到hdfs
    //svm_model.save(sc,"hdfs://namenode:9000/model/SVM")

  }

}
