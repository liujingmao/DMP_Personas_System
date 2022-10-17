name := "imocc-dmp-spark"

version := "0.1"

scalaVersion := "2.11.12"


libraryDependencies ++= Seq(
  // Spark 依赖
  "org.apache.spark" %% "spark-mllib" % "2.3.0"
    exclude("com.google.guava","guava")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),
  "org.apache.spark" %% "spark-core" % "2.3.0"
    exclude("com.google.guava","guava")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),
  "org.apache.spark" %% "spark-sql" % "2.3.0"
    exclude("com.google.guava","guava")
    exclude("org.slf4j","slf4j-log4j12"),

  // 降低 guava 的版本
  "com.google.guava" % "guava" % "15.0",

  // Phoenix 依赖
  "org.apache.phoenix" % "phoenix-core" % "4.13.1-HBase-1.2"
    exclude("com.google.guava","guava")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),
  "org.apache.phoenix" % "phoenix-spark" % "4.13.1-HBase-1.2"
    exclude("com.google.guava","guava")
    exclude("javax.servlet","*")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),


  // hbase依赖
  "org.apache.hbase" % "hbase-server" % "1.2.6"
    exclude("javax.servlet","*")
    exclude("com.google.guava","guava")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),

  // hive依赖
  "org.apache.spark" %% "spark-hive" % "2.3.0"
    exclude("com.google.guava","guava")
    exclude("javax.servlet","*")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),
//  "org.apache.hadoop" % "hadoop-client" % "2.7.4",
  "org.apache.hive" % "hive-jdbc" % "2.3.2"
    exclude("com.google.guava","guava")
    exclude("javax.servlet","*")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),
  "org.apache.hive" % "hive-metastore" % "2.3.2"
    exclude("com.google.guava","guava")
    exclude("javax.servlet","*")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),

  //结巴 依赖
  "com.huaban" % "jieba-analysis" % "1.0.2"
    exclude("com.google.guava","guava")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),

  //xgboost 依赖 注意xgboost4j-spark和spark以及scala版本的对应关系·
  "ml.dmlc" % "xgboost4j-spark" % "0.90"
    exclude("com.google.guava","guava")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),

  //es 依赖
  "org.elasticsearch" % "elasticsearch-hadoop" % "7.10.0"
    exclude("com.google.guava","guava")
    exclude("javax.servlet","*")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),


  //hive-hbase映射表依赖
  "org.apache.hive" % "hive-hbase-handler" % "2.3.1"
    exclude("com.google.guava","guava")
    exclude("javax.servlet","*")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),

  //map转json 依赖
  "org.json4s" %% "json4s-native" % "3.2.11"
    exclude("com.google.guava","guava")
    exclude("javax.servlet","*")
    exclude("ch.qos.logback","*")
    exclude("org.slf4j","slf4j-log4j12"),

  //log4j2
  "org.apache.logging.log4j" % "log4j-core" % "2.6.2",
  "org.apache.logging.log4j" % "log4j-api" % "2.6.2",

  "org.apache.commons" % "commons-lang3" % "3.8",

)
