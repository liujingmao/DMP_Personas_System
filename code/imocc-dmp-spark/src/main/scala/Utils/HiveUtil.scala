package Utils

import config.ImoccSparkConf.spark
import org.apache.spark.sql.{DataFrame, SaveMode}

/**
 * Hive工具类
 */
object HiveUtil {

  /**
   * 写入Hive
   * @param dataSet
   * @param tableName
   */
  def hiveSave(dataSet:DataFrame,tableName:String): Unit ={
    //建立临时表
    dataSet.createOrReplaceTempView("table_tmp")
    spark.sql("select * from table_tmp")
      .write
      .mode(SaveMode.Overwrite)
      .format("Hive")
      .saveAsTable(tableName)
    //注销临时表
    spark.catalog.dropTempView("table_tmp")
  }

  /**
   * 读取hive
   * @param sql
   * @return
   */
  def hiveRead(sql:String): DataFrame ={
    spark.sql(sql)
  }
}
