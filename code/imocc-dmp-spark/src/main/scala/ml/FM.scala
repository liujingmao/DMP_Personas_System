package ml

import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.mllib.linalg.{Vector => MLLibVector, Vectors => MLLibVectors}
import org.apache.spark.mllib.regression.LabeledPoint
import breeze.linalg.{DenseMatrix}
import breeze.numerics._

import scala.util.Random

/**
 *
 * Spark3.0: FMClassifier
 *
 * FM公式:
 * w_0 + sum(w_n*x_n) + sum(<v_i,v_j>*x_i*x_j)
 *
 * FM计算流程：
 * 1. 初始化
 * 初始化偏置向量 w_0
 * 初始化权重系数 w_1,w_2,...,w_n
 * 初始化v系数矩阵
 *
 * 2.对每个样本循环：
 * 计算w_0
 * 计算w_n
 * 计算v_i_f
 *
 * 3.重复第2步，直到满足条件
 *
 *
 */

class FM {
  //
  /**
   * 模型训练
   * @param dataSet
   * @param k  隐向量的长度
   * @param iter  迭代次数
   * @return
   */
  def train(dataSet: DataFrame,k:Int,iter:Int): (Double, MLLibVector, DenseMatrix[Double]) = {

    //学习率
    val alpha = 0.01

    //数据集
    val data = dataSet.select(col("label"), col("features"))
    //构造LabelPoint
    val lpdata = data.rdd.map {
      case Row(label: Double, features: Vector) =>
        //ml vector 转化 mllib vector
        val mllibFeatures: MLLibVector = MLLibVectors.fromML(features)
        LabeledPoint(label, mllibFeatures)
    }
    println("##########构造LabelPoint############")
    lpdata.foreach(println(_))

    //特征维度数量，即有多少个特征
    val numFeatures = lpdata.first().features.size
    println("特征维度数量=" + numFeatures)
    //初始化偏置向量w_0
    var w_0 = 0.0
    //由于要用到矩阵的计算，所以采用breeze的矩阵
    //初始化权值系数w_n
    var w_n = MLLibVectors.dense(Array.fill(numFeatures)(0.0))
    //    var w_n:DenseMatrix[Double] =
    //    DenseMatrix.zeros[Double](numFeatures,1)
    //初始化v系数矩阵
    //v系数矩阵：每一行是每个特征的隐向量v_i,v_i的维度是k
    //Random.nextGaussian()返回的是服从高斯分布的随机数
    var v: DenseMatrix[Double] =
    new DenseMatrix[Double](numFeatures, k, Array.fill(k * numFeatures)(Random.nextGaussian() * 0.01))
    println("##########初始化v系数矩阵#############")
    println(v)

    //按迭代次数进行循环
    for (i <- 0 until iter) {
      println("第" + i + "次迭代")
      //遍历数据集
      lpdata.foreach(line => {
        val label = line.label
        val features: MLLibVector = line.features
        //计算交叉项
        var interaction = 0.0 //交叉项
        var inter_1 = Array.fill(k)(0.0)
        var inter_2 = 0.0
        for (_k <- 0 until k) {
          //这里一定要理解 v_i_f 的 f 指的是什么
          println("f=" + _k)
          //遍历特征维度
          //是计算所有f的sum(v_i_f*x_i)和sum(x_i^2*v_i_f^2)
          features.foreachActive {
            (index, value) =>
              println("特征" + index + "=" + value)
              //按照交叉项最终的展开公式进行计算

              //计算x_i*v_i_f的点积
              val d = value * v(index, _k)
              inter_1(_k) += d

              //计算x_i^2与v_i_f^2的点积
              inter_2 += d * d
          }
          //根据交叉项的展开公式
          //计算 0.5 * (x_i*v_i)*(x_i*v_i) - x_i^2与v_i^2
          interaction += 0.5 * inter_1(_k) * inter_1(_k) - inter_2
        }
        //交叉项求出之后，做初步的预测
        //        val breeze_features = new DenseVector[Double](Array(2.0,1.0))
        val p = w_0 + /*w_n * features*/ +interaction
        //对损失函数求导
        // sigmoid(-y_预测 * y_真实值) -1.0
        // sigmoid = 1/1-exp(x)
        val dloss = 1.0 / (1.0 - Math.exp(-p * label)) - 1.0

        //通过随机梯度下降更新参数 w_0,w_n,v
        //以下的公式是按照通过随机梯度下降求解参数的最终公式
        //公式中的w_0求导
        val dloss_w_0 = dloss * 1
        //更新w_0
        w_0 = w_0 - alpha * dloss_w_0


        val wRange = Range(numFeatures * k, numFeatures * k + numFeatures)
        features.foreachActive {
          (index, value) =>
            if (wRange.contains(index)) {
              //公式中的w_n求导
              //val dloss_w_n = dloss * value
              //w_n(index) += w_n(index) - alpha * dloss_w_n
            }
          //公式中v_i_f求导
        }
      })
    }

    //返回经过迭代最后更新好的w_0,w_n,v
    (w_0, w_n, v)
  }

  //预测
  /**
   * 把更新完成的w_0,w_n,v这3个参数输入公式进行预测
   *
   * @param dataSet
   * @param w_0
   * @param w_n
   * @param v
   */
  def predict(dataSet: DataFrame, w_0: Double, w_n: MLLibVector, v: DenseMatrix[Double], k: Int): Unit = {
    //sigmoid的阙值
    //小于阙值是负例，大于阙值的是正例
    val threshold = 0.5

    //数据集
    val data = dataSet.select(col("label"), col("features"))
    //构造LabelPoint
    val lpdata = data.rdd.map {
      case Row(label: Double, features: Vector) =>
        //ml vector 转化 mllib vector
        val mllibFeatures: MLLibVector = MLLibVectors.fromML(features)
        LabeledPoint(label, mllibFeatures)
    }

    //遍历数据集
    lpdata.foreach(line => {
      val label = line.label
      val features = line.features

      var interaction = 0.0 //交叉项
      var inter_1 = Array.fill(k)(0.0)
      var inter_2 = 0.0
      //遍历特征维度
      for (_k <- 0 until k) {
        //这里一定要理解 v_i_f 的 f 指的是什么
        println("f=" + _k)
        //遍历特征维度
        //是计算所有f的sum(v_i_f*x_i)和sum(x_i^2*v_i_f^2)
        features.foreachActive {
          (index, value) =>
            println("特征" + index + "=" + value)
            //按照交叉项最终的展开公式进行计算

            //计算x_i*v_i_f的点积
            val d = value * v(index, _k)
            inter_1(_k) += d

            //计算x_i^2与v_i_f^2的点积
            inter_2 += d * d

            //计算 0.5 * (x_i*v_i)*(x_i*v_i) - x_i^2与v_i^2
            interaction += 0.5 * inter_1(_k) * inter_1(_k) - inter_2
        }
      }
      //预测
      val p = w_0 + /*w_n * features +*/ interaction
      var predict = 1
      if (p > threshold) {
        predict = 1
      } else if (p < threshold) {
        predict = -1
      }
      println("######预测########")
      println("特征："+features)
      println("预测label="+predict)
    })
  }
}
