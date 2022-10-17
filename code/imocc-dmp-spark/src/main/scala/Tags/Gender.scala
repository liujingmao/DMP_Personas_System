package Tags

import Utils.{SparkUtils, TagUtils}
import ml.NaiveBayesModel
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

/**
 * 性别模型
 */
class Gender {
  /**
   * 通过用户行为性别预测
   * @param dataSet
   */
  def genderForecast(dataSet:DataFrame): DataFrame ={

    /**
     * 特征选取
     * 购买的最多的类别
     * (购买的第二多的类别)
     * (购买的第三多的类别)
     * 消费次数
     * 消费总金额
     *
     */
    val data = featureHandle(dataSet)

    /**
     * 特征处理
     */

    //将消费次数，消费总金额离散化
    //转换为多元的类别变量
    val bucketed_data = SparkUtils.bucketizer(
      data,
      Array("total_amount","total_order"),
      Array("bucketed_amount","bucketed_order"),
      Array(
        //Double.NegativeInfinity和Double.PositiveInfinity在不清楚上下限的情况下使用·
        //阈值 x,y 范围是在[x,y)

        //消费总金额 < 100 归为 0.0
        //消费总金额 >= 100 && <200 归为 1.0
        //消费总金额 >= 200 && <500 归为 2.0
        //消费总金额 >= 500 && <1000 归为 3.0
        //消费总金额 >= 1000 && <2000 归为 4.0
        //消费总金额 >= 2000 && <5000 归为 5.0
        Array(
          Double.NegativeInfinity,
          100,200,500,1000,2000,5000,
          Double.PositiveInfinity),
        //消费订单数 < 10 归为 0.0
        //消费订单数 >= 10 && <20 归为 1.0
        //消费订单数 >= 20 && <30 归为 2.0
        //消费订单数 >= 30 && <40 归为 3.0
        Array(
          Double.NegativeInfinity,
          10,20,30,40,
          Double.PositiveInfinity)
      )
    )

      //品类id文本特征索引

      val stringIndexer = new StringIndexer()
        .setInputCol("category_id")
        .setOutputCol("indexer_category_id")
        //忽略null
        .setHandleInvalid("skip")
        .fit(bucketed_data)
        .transform(bucketed_data)

    println("############品类id文本特征索引##############")
    stringIndexer.show(false)

    //合并为单一特征列
    val data_assembler = new VectorAssembler()
      .setInputCols(Array("bucketed_amount","bucketed_order","indexer_category_id"))
      .setOutputCol("features")
      .transform(stringIndexer)
        .select(col("user_id"),
          col("gender"),
          col("features"))


    println("############合并为单一特征列##############")
    data_assembler.show(false)

    //朴素贝叶斯进行性别预测
    val model = new NaiveBayesModel()
    val prediction = model.train(data_assembler,"features","gender")

    //打标签

    /**
     * 0 男性
     * 1 女性
     */

    val gender_rule = when(col("prediction")===0,0)
      .when(col("prediction")===1,1)

    val _gender_tag = prediction.withColumn("tag_rule",gender_rule)

    val gender_tag = TagUtils.taggingByRule(_gender_tag,"性别")

    println("########性别标签#############")
    val hive_gender_tag = gender_tag.select(col("user_id"),
        col("tag_name_cn").as("tag_id"),
        col("tag_name").as("tag_value"))
    hive_gender_tag.show(false)

    //保存到hive数仓的用户标签流水表

    //    val hive_table = "dw.dws_user_action_tags_map_all"
    //
    //    println("########保存用户性别标签#############")
    //    HiveUtil.hiveSave(hive_gender_tag,hive_table)

    gender_tag

  }

  /**
   * 计算购买的最多的类别，消费次数，消费总金额
   * @param dataSet
   * @return
   */
  def featureHandle(dataSet:DataFrame): DataFrame ={

    //这部分逻辑有比较多的join,运行时间会比较长
    //这部分逻辑应该放到离线进行运算，把结果写到hive，使用的直接读取
    //这里为了演示，就写到这里

    //计算消费次数，消费总金额
    val _data = dataSet.groupBy(col("user_id"),col("gender"))
      .agg(
        sum(col("price")).as("total_amount"),
        count(col("*")).as("total_order")
      )

    println("######消费次数，消费总金额##########")
    _data.show(false)
    println("_data总条数="+_data.count())

    //计算购买的最多的类别

    val _data2 = dataSet.groupBy(
      col("user_id"),
      col("category_id"))
      .agg(count(col("*")).as("count_category"))
        .orderBy(desc("count_category"))

    println("######每个用户购买的类别次数##########")
    _data2.show(false)
    println("_data2总条数="+_data2.count())

    val _data3 = _data2.groupBy(col("user_id"))
      .agg(max(col("count_category")).as("max_category"))
      .orderBy(desc("max_category"))

    println("######获取每个用户购买的最多次数类别##########")
    _data3.show(false)
    println("_data3总条数="+_data3.count())

    val _data4 = _data3.join(_data2,Seq("user_id"),"left")
      .where(
        _data3.col("max_category")
          ===
        _data2.col("count_category")
      )
      .orderBy(asc("max_category"))

    println("######获取每个用户购买的最多次数类别id##########")
    _data4.show(30,false)
    println("_data4总条数="+_data4.count())

    val _data5 = SparkUtils.join(_data,_data4,Seq("user_id"))

    println("######合并购买的最多的类别，消费次数，消费总金额##########")
    _data5.show(30,false)
    println("_data5总条数="+_data5.count())

    _data5

  }

}
