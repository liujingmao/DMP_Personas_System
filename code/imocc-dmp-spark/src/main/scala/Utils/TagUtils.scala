package Utils

import java.time.{LocalDate, Period}

import config.ImoccSparkConf.properties
import org.apache.spark.sql.DataFrame

/**
 * 标签工具类
 */
object TagUtils {

  //标签表
  case class Tags(
                   tag_rule: Int,
                   tag_id: Int,
                   tag_level: Int,
                   tag_name: String,
                   tag_level_second: Int,
                   tag_id_string: String,
                   tag_name_cn: String
                 )

  /**
   * 根据规则获取对应的标签
   *
   * @param dataSet
   * @return
   */
  def taggingByRule(dataSet: DataFrame, tag_name_cn: String): DataFrame = {
    //集群运行 读取hive数仓标签维度表
//    val tags = HiveUtil.hiveRead("select * from dwd.dim_tags")

    //本地运行 读取标签表
    val tags = SparkUtils.getDataSetByCSV[Tags](properties.getProperty("tags_file"))


    //标签应该是有2-3个层级
    //这里只使用了一级层级确定标签

    dataSet.join(tags, "tag_rule")
      .where(tags.col("tag_name_cn") === tag_name_cn)
  }

  /**
   * 时间衰减因子计算
   * @param old_weight 上一次标签权重值
   * @param year 上一次标签权重值计算的年
   * @param month 上一次标签权重值计算的月
   * @param day 上一次标签权重值计算的日·
   * @return
   */
  def timeExp(old_weight:Double,
              year:Int,
              month:Int,
              day:Int): Double ={

    //间隔时间
    val now:LocalDate = LocalDate.now()
    val old_time = LocalDate.of(year,month,day)
    val time_interval = Period.between(now,old_time).getDays

    /**
     * 冷却系数可以设置为常数
     * 冷却系数是根据业务设定，不同业务的冷却系数不同
     * 消费频次高，替代性高的产品，冷却系数会设置比较高
     *
     * 一般企业冷却系数的设定：
     * 设定初始的标签权重
     * 设定一个时间间隔
     * 设定经过这个时间间隔后标签权重
     * 通过以上3个值确定冷却系数
     *
     */
    val dacey = 0.125


    //时间衰减因子计算公式
    //当前标签权重值 = 上一次标签权重值 * math.exp(-冷却时间*时间间隔)
    val new_weight = old_weight* math.exp(-dacey*time_interval)

    new_weight

  }

}
