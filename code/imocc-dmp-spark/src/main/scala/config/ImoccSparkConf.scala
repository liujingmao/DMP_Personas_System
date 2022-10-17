package config

import java.util.Properties

import com.huaban.analysis.jieba.JiebaSegmenter
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * SparkSession 配置类
 */
case object ImoccSparkConf {

  val sparkConf = new SparkConf()
    .registerKryoClasses(Array(classOf[JiebaSegmenter]))
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    //设置自动创建es索引
    .set("es.index.auto.create", "true")
    //es节点
    .set("es.nodes","192.168.137.47")
    //es端口
    .set("es.port","9201")
    //hbaseApi使用的zookeeper配置

  val spark:SparkSession = SparkSession.builder()
    .appName("imocc-dmp-spark")
    .master("local[*]")
    .config(sparkConf)
    //*** 集群运行打开，本地运行关闭
    //*** 读取hive-site.xml
    //.enableHiveSupport()
    .getOrCreate()

  // 注意是 hadoop 包下的 Configuration
  val hadoop_conf = new Configuration()

  val sc = spark.sparkContext
  sc.hadoopConfiguration.set("dfs.client.use.datanode.hostname", "true");

  //日志级别设置
  //修改Resources/log4j2.xml

  //这是java util 包下的 Properties
  val properties = new Properties()
  val properties_file = ImoccSparkConf
    .getClass
    .getClassLoader
    .getResourceAsStream("imocc.properties")
  properties.load(properties_file)
}
