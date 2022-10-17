import config.ImoccSparkConf.{properties, spark}
import Utils.SparkUtils
import ml.FM
import org.apache.spark.ml.feature.VectorAssembler


/**
 * 实现FM算法
 */
object FMApplication {
  case class FMTest(
                   //标签类别
                   label:Double,
                   //特征1
                    feature1:Double,
                   //特征2
                    feature2:Double)
  def main(args: Array[String]): Unit = {

    //本地读取fm的训练数据集
    val _dataSet =
      SparkUtils.getDataSetByCSV[FMTest](properties.getProperty("fm_test_file"))
    println("#########fm的训练数据集##########")
    println(_dataSet.show(false))

    //合并特征1，特征2
    val dataSet = new VectorAssembler()
      .setInputCols(Array("feature1", "feature2"))
      .setOutputCol("features")
        .transform(_dataSet)
    println("#########合并特征1，特征2##########")
    println(dataSet.show(false))

    val fm = new FM()
    //设置超参
    val k= 5  //隐向量长度
    val iter =5  //迭代次数
    //fm模型训练
    val(w_0,w_n,v) = fm.train(dataSet,k,iter)
    //这里预测就用同一个数据集
    fm.predict(dataSet,w_0,w_n,v,k)

    spark.stop()
  }
}
