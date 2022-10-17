/spark/bin/spark-submit --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties" --master spark://spark-master:7077 --class SentimentApplication SentimentApplication.jar
