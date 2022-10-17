package ml

import Utils.SparkUtils
import ml.dmlc.xgboost4j.scala.spark.XGBoostClassifier
import ml.dmlc.xgboost4j.scala.spark.TrackerConf
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.sql.DataFrame

/**
 * xgboost算法模型
 */
class XgboostModel {

  def train(dataSet:DataFrame,featureCol:String,labelCol:String): Unit ={

    //数据集划分
    val Array(train,test) = SparkUtils.dataSplit(dataSet,0.8,0.2)

    //设置xgboost在Spark集群运行的参数
    val xgbParam = Map(
      "eta" -> 0.023f,   //学习率
      "max_depth" -> 5, //树的最大深度
      //objective参数除了二分类外，
      // 还有multi:softmax,multi:softprod,这两个指代是多分类
      "objective" -> "binary:logistic",  //目标函数，二分类逻辑回归·
      //如果objective指定了二分类，则num_class必须注释掉
      // "num_class" -> 2, //类别
      "num_round" -> 300,   //迭代次数
      //xgboost的线程数量，小于或等于Spark的executor的数量
      //若是本地运行，则是小于.master("local[*]")所设置的线程数
      "num_workers" -> 1,
      //改为走 scala分支，xgboost默认是走python分支
      "tracker_conf" -> TrackerConf(0L, "scala"),
      "seed" -> 2020,//随机数种子
      "colsample_bytree" -> 0.85,//构造每个树时列的下采样比例
      "subsample" -> 0.85, //训练集的子样本比例,XGBoost会在生长树之前随机抽取一定比例的训练数据。这样可以防止过度拟合。
      "disable_default_eval_metric " -> 1, //禁用默认度量标准
      "verbosity" -> 2 //0 (silent), 1 (warning), 2 (info), 3 (debug)。
    )

    //模型
    val xgb = new XGBoostClassifier(xgbParam)
      .setFeaturesCol(featureCol)
      .setLabelCol(labelCol)
      .setTreeMethod("approx") //如果放在Spark集群运行，这个参数必须设置
      .setEvalMetric("logloss") //自定义评测方法
      .setNumEarlyStoppingRounds(10) //防止因为迭代次数过多而过拟合
      .setMaximizeEvaluationMetrics(false) //是否要在训练中最大化或最小化指标

    //训练
    val xgb_model = xgb.fit(train)
    //打印训练日志
    println(xgb_model.summary.toString())

    //预测
    val predictions = xgb_model.transform(test)
    println("##########预测############")
    predictions.show(false)

    //评估AUC
    val evaluator = new BinaryClassificationEvaluator()
    evaluator.setLabelCol("label")
    evaluator.setRawPredictionCol("probability")
    evaluator.setMetricName("areaUnderROC")

    val AUC = evaluator.evaluate(predictions)
    println("AUC："+AUC)

    //特征筛选

    //通过信息增益来计算特征的重要性
    //特征分数在训练模型过程中累计此特征的信息增益得到的
    //参数gain表示是计算方式采用信息增益
    val featureScore_gain =
    xgb_model.nativeBooster.getScore("","gain")
    val sortedScoreMap = featureScore_gain.toSeq.sortBy(-_._2)
    println("###########通过增益来计算特征的重要性###########")
    println("###########其中键是特征索引（例如：f0，f1，f2 ...）###########")
    println("###########value是特征分数（通过累积每个特征的信息增益来计算）###########")
    //getScore返回的特征重要性的映射 (Map[特征索引:String,特征分数:Int])
    //其中键是特征索引（例如：f0，f1，f2 ...）
    println(sortedScoreMap)
    for(i<-0 until sortedScoreMap.size) {
      val map = sortedScoreMap(i)
      println(map._1.toString+" : "+map._2.toDouble)
    }

    //在集群运行 保存模型
    //xgb_model.nativeBooster.saveModel("hdfs://namenode:9000/model/xgboost")

    //通常会把每次训练得到的 AUC 也存储到hdfs
    //下次训练后，首先进行评估上一次的AUC和本次AUC
    //如果相差超过一定的阙值，则判断本次训练模型存在问题
  }
}
