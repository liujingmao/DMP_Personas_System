package Utils

import com.huaban.analysis.jieba.{JiebaSegmenter, SegToken}
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode
import config.ImoccSparkConf.spark
import org.apache.spark.ml.feature.{Binarizer, Bucketizer, StopWordsRemover}
import org.apache.spark.mllib.linalg.{Vectors,Vector}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.catalyst.ScalaReflection.universe._
import org.apache.spark.sql.functions._


/**
 * Spark工具类
 */
object SparkUtils {


  /**
   * 从csv读取数据集，这个是为了本地运行测试
   * @param file 文件名
   * @tparam T 泛型
   * @return
   */
  def getDataSetByCSV[T:TypeTag](file:String):DataFrame={

    val data = spark.read.format("csv")
      .option("header",true)
      .option("delimiter",",")
      .option("inferSchema",false)
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .schema(ScalaReflection.schemaFor[T]
        .dataType.asInstanceOf[StructType])
      .load(file)

    data

  }

  /**
   * 结巴分词
   * @param dataSet 数据集
   * @param inputCol 需要分词的列
   * @param outputCol 输出列
   */
  def jieba(dataSet:DataFrame,inputCol:String,outputCol:String): DataFrame ={

    val segmenter = new JiebaSegmenter()
    val seg = spark.sparkContext.broadcast(segmenter)
    val jieba_udf = udf{(text:String)=>
      val _seg = seg.value
      //JiebaSegmenter有两个方法：process和sentenceProcess
      //process需要两个参数：要分词的句子和切分模式(INDEX和SEARCH)
      //sentenceProcess只需要一个参数：要分词的句子
      _seg.process(text.toString,SegMode.INDEX)
        //把切好的词装进数组
        .toArray().map(_.asInstanceOf[SegToken].word)
        //使用"/"连接切好的词
        .filter(_.length>1).mkString("/").split("/").toSeq
    }

    val data =
      dataSet.withColumn(outputCol,jieba_udf(col(inputCol)))

    data
  }


  /**
   * 数据集切割训练集和测试集
   * @param dataSet
   * @param train  训练集所占比例
   * @param test   测试集所占比例
   * @return
   */
  def dataSplit(dataSet:DataFrame,train:Double,test:Double): Array[DataFrame] ={
    dataSet.randomSplit(Array(train,test),seed = 11L)
  }

  /**
   * join
   * @param small_data
   * @param big_data
   * @param on
   * @return
   */
  def join(small_data:DataFrame,big_data:DataFrame,on:Seq[String]): DataFrame ={
    small_data.join(big_data,on,"left")
  }

  /**
   * 将连续特征转化为多元的类别变量
   * @param dataSet
   * @param inputCol
   * @param outputCol
   * @param splits
   * @return
   */
  def bucketizer(dataSet:DataFrame,
                 inputCol:Array[String],
                 outputCol:Array[String],
                 splits:Array[Array[Double]]): DataFrame ={

    //分桶
    val bucketizerDataFrame = new Bucketizer()
      .setInputCols(inputCol)
      .setOutputCols(outputCol)
      .setSplitsArray(splits) //阙值 分成多少个桶
      .transform(dataSet)

    println("##########连续特征转化为多元的类别变量###############")
    bucketizerDataFrame.show(false)
    bucketizerDataFrame
  }


  /**
   *
   * @param dataSet
   * @return
   */
  def mlVec2mllibVec(dataSet:DataFrame): RDD[Vector] ={
    val labelPoitFeatures = dataSet.rdd.map({
      case Row(label:Double,features:org.apache.spark.ml.linalg.Vector)
      => {
        //将ml中的Vector转化为mllib中的Vector
        val arr = features.toArray
        val vec = Vectors.dense(arr)
        vec
      }
    })
    labelPoitFeatures
  }

  /**
   * 获取Option对象的值
   * @param x
   * @return
   */
  def getOptionValue(x:Option[Int])=x match {
    case Some(s) => s
    case None => 0
  }

}
