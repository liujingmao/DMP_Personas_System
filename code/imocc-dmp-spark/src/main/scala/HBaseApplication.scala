import Utils.HBaseUtil
import org.apache.spark.sql.functions._

/**
 * HBase 操作
 * 注意，若在本地运行，要在本地配置以下host ：
 * zoo1 zoo2 zoo3
 * hbase-master
 * hbase-regionserver-1
 * hbase-regionserver-2
 * hbase-regionserver-3
 */
object HBaseApplication {
  def main(args: Array[String]): Unit = {

    //这张表是环境搭建的时候建立的，具体查看表结构和数据导入这个文档
    val phoenix_table_name = "\"user_test\""
    //hbase的用户标签映射表
    val hbase_table_name="user_tags_map_all"

    //通过phoenix jdbc读取
    val phoenix_jdbc_read = HBaseUtil.readByJDBC(phoenix_table_name)
    println("====通过phoenix jdbc读取===")
    phoenix_jdbc_read.select(col("name")).show()

    //通过phoenix api读取
    val phoenix_api_read = HBaseUtil
      .readBySqlApi(phoenix_table_name,Array("name"))
    println("====通过phoenix api读取===")
    phoenix_api_read.select(col("name")).show()

    //通过HBase Api读取
    val hbase_read = HBaseUtil.readByHBaseApi(
      hbase_table_name,"200005","200010")
  }
}
