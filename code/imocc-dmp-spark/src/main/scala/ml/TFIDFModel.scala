package ml

import Utils.{HiveUtil, SparkUtils}
import org.apache.spark.ml.feature.{HashingTF, IDF}
import org.apache.spark.sql.DataFrame

import scala.collection.mutable.ListBuffer
import util.control.Breaks._

/**
 * TF-IDF
 */
class TFIDFModel {

  /**
   * 使用Spark ML来计算TF-IDF
   * @param dataSet   数据集
   * @param inputCol  输入列
   * @param outputCol 输出列
   * @return
   */
  def train(dataSet: DataFrame, inputCol: String, outputCol: String): DataFrame = {

    //HashingTF将词条集合转化为相同长度的特征向量
    //HashingTF在哈希的同时计算词频
    val hashingTF = new HashingTF()
      .setInputCol(inputCol)
      .setOutputCol("words_hash")
      .setNumFeatures(200) //最大的特征数(哈希表的桶数)

    val tfData = hashingTF.transform(dataSet)
    println("########各词条tf值############")
    tfData.show(false)

    val idf = new IDF()
      .setInputCol("words_hash")
      .setOutputCol(outputCol)

    val idfModel = idf.fit(tfData)
    val tfidf = idfModel.transform(tfData)
    println("########TF-IDF度量值############")
    tfidf.show(false)
    tfidf.printSchema()

    tfidf

  }

  /**
   *
   * 通过tf-idf计算标签权重
   * 通过tf-idf公式计算tf-idf
   *
   * 标签看作是单词
   * 指定用户被标注的标签看作是文档
   * 所有用户被标注的标签看作是文档集
   *
   */

  /**
   * 计算tf(词频)
   *
   * @param user_tags  Map[标签id,被标注的次数]
   * @param target_tag_id 指定标签的id
   * @return
   */
  def tf(user_tags:Map[Int,Int],target_tag_id:Int): Double ={

    //计算指定用户被标注指定标签的次数
    //get(target_tag_id)获取到的是Option对象，
    //get(target_tag_id).get才能获取到Option对象里面的值
    //val the_user_tag_count = user_tags.get(target_tag_id).get
    println("用户被标注的标签："+user_tags)
    val the_user_tag_count = SparkUtils.getOptionValue(user_tags.get(target_tag_id))
    println("指定用户被标注指定标签的次数="+the_user_tag_count+",tag_id="+target_tag_id)

    //计算指定用户被标注标签的总次数
    val the_user_tags_count = user_tags.map(_._2).reduce(_+_)
    println("指定用户被标注标签的总次数="+the_user_tags_count)

    val tf = the_user_tag_count.toFloat / the_user_tags_count.toFloat
    println("tag_id="+target_tag_id+",tf="+tf)

    tf

  }

  /**
   * 计算df(文件频率)
   * @param all_user_tags Map[(用户id,商品id),Map[标签id,被标注的次数]]
   * @param target_tag_id 指定标签的id
   * @return
   */
  def df(all_user_tags:Map[(String,String),Map[Int,Int]],target_tag_id:Int): Double ={

    //计算所有用户被标注指定标签的总次数
    var count = 0
    all_user_tags.map(_._2).foreach(x=>{
      //获取指定标签被标注的次数
      val the_tag_count = SparkUtils.getOptionValue(x.get(target_tag_id))

      count += the_tag_count
    })

    println("tag_id="+target_tag_id+",df="+count)
    count
  }

  /**
   * 计算idf(逆向文件频率)
   * @param all_user_tags Map[(用户id,商品id),Map[标签id,被标注的次数]]
   * @param target_tag_id  指定标签的id
   * @return
   */
  def idf(all_user_tags:Map[(String,String),Map[Int,Int]],
          target_tag_id:Int): Double ={

    //计算所有用户被标注标签的总次数
    var count = 0
    all_user_tags.map(_._2)
      .foreach(x=>{
        count = x.map(_._2).reduce(_+_)
      })
    println("所有用户被标注标签的总次数="+count)

    //思考：为什么PPT的公式不需要加上log，而这里需要呢？
    //加上log防止权重爆炸
    //idf的分母的值很小，分子的值很大，不加log,会造成idf的值非常大，影响了权重·
    val df_value = df(all_user_tags,target_tag_id)
    var idf = 0.0
    if(df_value !=0 ) {
      idf = Math.log(count / df_value)
    }

    println("tag_id="+target_tag_id+",idf="+idf)
    idf

  }

  /**
   * 计算tf-idf
   * @param user_tags 单个用户被标注的行为标签和标注次数
   * @param tags  Array[标签id]
   * @param all_user_tags 所有用户被标注的行为标签和标注次数
   * @param user_id 用户id
   * @param item_id 商品id
   * @return ListBuffer[Tuple(tfidf值,行为标签id,行为标签id被标注次数)]
   */
  def tfidf(user_tags:Map[Int,Int],
            tags:Array[Int],
             all_user_tags:Map[(String,String),Map[Int,Int]],
           user_id: String,
            item_id: String
            ): ListBuffer[Tuple3[Double,Int,Int]] ={
    var list = ListBuffer[Tuple3[Double,Int,Int]]()

    tags.foreach(target_tag_id=>{
      val tfidf_weight =
        tf(user_tags,target_tag_id) * idf(all_user_tags,target_tag_id)
      println("user_id="+ user_id+
        "item_id="+ item_id+
        "tag_id="+ target_tag_id+
        ",tfidf="+ tfidf_weight)

      val rs = (tfidf_weight,
        target_tag_id,
        SparkUtils.getOptionValue(user_tags.get(target_tag_id)))
      list += rs
    })

    println("各个标签对于user_id="+user_id+",item_id="+item_id+"的tfidf值")
    for(i<-list) {
      println(i)
    }

    list


  }


}
