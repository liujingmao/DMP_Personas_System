package ml

import Utils.SparkUtils
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.DataFrame

/**
 * 朴素贝叶斯模型
 */
class NaiveBayesModel {

  /**
   * 模型训练
   * @param dataSet
   * @param featureCol
   * @param labelCol
   * @return
   */
  def train(dataSet:DataFrame,featureCol:String,labelCol:String): DataFrame ={

    val Array(train,test) = SparkUtils.dataSplit(dataSet,0.8,0.2)

    //朴素贝叶斯模型
    val nb = new NaiveBayes()
      .setFeaturesCol(featureCol)
      .setLabelCol(labelCol)
      .setSmoothing(1.0) //平滑参数 (拉普拉斯平滑)

    //模型训练
    val model = nb.fit(train)
    val predictions = model.transform(test)
    println("#########朴素贝叶斯模型预测性别##########")
    predictions.show(false)

    //模型评估
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol(labelCol)
      .setPredictionCol("prediction")
      .setMetricName("accuracy")

    val accuracy = evaluator.evaluate(predictions)
    println("准确率="+accuracy)

    //保存朴素贝叶斯模型到hdfs
    //model.save("hdfs://namenode:9000/model/NB")

    predictions
  }
}
