package Utils

import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.phoenix.spark._
import config.ImoccSparkConf.{sc, spark}
import spark.implicits._
import scala.collection.JavaConverters._
import config.ImoccSparkConf.hadoop_conf
import org.apache.hadoop.hbase.{Cell, HBaseConfiguration}
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.rdd.RDD

/**
 * HBase 工具类
 */
object HBaseUtil {

  val ZOOKEEPER_URL = "zoo1:2182:/hbase"
  val conf = HBaseConfiguration.create()
  conf.set("hbase.zookeeper.quorum","zoo1:2182,zoo2:2183,zoo3:2184")



  /**
   * 通过 JDBC 读取 HBase
   * @param tableName
   * @return
   */
  def readByJDBC(tableName:String): DataFrame ={
      val data = spark.read.format("jdbc")
        .option("driver","org.apache.phoenix.jdbc.PhoenixDriver")
        .option("url","jdbc:phoenix:"+ZOOKEEPER_URL)
        .option("dbtable",tableName)
        .load()

      data
  }

  /**
   * 通过 JDBC 写入 HBase
   * @param dataSet
   * @param tableName
   */
  def writeByJDBC(dataSet:DataFrame,
                  tableName:String): Unit ={

    dataSet.write.format("org.apache.phoenix.spark")
      .mode(SaveMode.Overwrite)
      .option("table",tableName)
      .option("zkUrl",ZOOKEEPER_URL)
      .save()

  }

  /**
   * 通过 Phoenix SqlApi 读取 HBase
   * @param tableName
   * @param fields
   * @return
   */
  def readBySqlApi(tableName:String,
                   fields:Array[String]): DataFrame = {

    val data = spark.sqlContext.phoenixTableAsDataFrame(
      tableName,
      fields,
      conf=hadoop_conf,
      zkUrl = Some(ZOOKEEPER_URL)
    )

    data

  }

  /**
   * 通过 newAPIHadoopRDD 读取 HBase
   */
  def readByHBaseApi(
                      table_name:String,
                      rowKeyStart:String,
                      rowKeyEnd:String)
  : RDD[(ImmutableBytesWritable,Result)] = {

    //表名
    conf.set(TableInputFormat.INPUT_TABLE,table_name)
    //rowKey开始
    conf.set(TableInputFormat.SCAN_ROW_START,rowKeyStart)
    //rowKey结束
    //范围是左闭区间右开区间
    conf.set(TableInputFormat.SCAN_ROW_STOP,rowKeyEnd)

    //读取数据
    val hbaseRDD = sc.newAPIHadoopRDD(
      conf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )

    //数据格式化
    println("共 "+hbaseRDD.count()+" 条")
    hbaseRDD.foreachPartition(fp=>{
      fp.foreach(f=>{
        val rowKey = Bytes.toString(f._2.getRow)
        println("===用户id====")
        println(rowKey)
        println("===用户标签====")
        //获取所有的cell
        val cells = f._2.listCells()
        //注意这里要将java的list转换为scala的list
        for(cell <- cells.asScala ) {
          //获取cell的字段名
          val tag_name = Bytes.toString(
                    cell.getQualifierArray,
                    cell.getQualifierOffset,
                    cell.getQualifierLength)
          //获取cell的值
          val tag_value = Bytes.toString(
            cell.getValueArray,
            cell.getValueOffset,
            cell.getValueLength)
          println(tag_name+" ："+tag_value)
        }
      })
    })
    hbaseRDD
  }


  /**
   * 通过 Phoenix SqlApi 写入 HBase
   * @param dataSet
   * @param tableName
   */
  def writeBySqlApi(dataSet:DataFrame,
                    tableName:String): Unit ={

    dataSet.saveToPhoenix(
      tableName,
      conf = hadoop_conf,
      zkUrl = Some(ZOOKEEPER_URL)
    )

  }

}
