import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object AppDS extends App {
  override def main(args: Array[String]) {
    val spark = SparkSession
      .builder()
      .appName("Nasa Logs Analyzer using Data Sets")
      .master("local[*]")
      .getOrCreate()

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._

    val LOG_REGEX = """^(.*) - - \[(.*)\].*\"(.*)\" (\d*) ([-|\d]*)$"""
    val DATE_FORMAT = """dd/MMM/yyyy:HH:mm:ss Z"""

    val logsDF = spark.read.text("/media/guilherme/backup/Documentos 2018/NasaLogsAnalyser/access_log_Jul95").toDF

    val lDs = logsDF.select(regexp_extract($"value", LOG_REGEX, 1) as "host",
      unix_timestamp(regexp_extract($"value", LOG_REGEX, 2), DATE_FORMAT).cast(TimestampType) as "collected_at",
      regexp_extract($"value", LOG_REGEX, 3) as "request",
      regexp_extract($"value", LOG_REGEX, 4).cast("Int") as "http_reply_code",
      when(regexp_extract($"value", LOG_REGEX, 5).isNotNull,
        regexp_extract($"value", LOG_REGEX, 5).cast("Long"))
        .otherwise(lit(0l)) as "reply_bytes")

    // Question 1. Número​ ​ de​ ​ hosts​ ​ únicos.
    val hostsUnicos = lDs.filter($"host".isNotNull).select("host").distinct.count()

    // Question 2. O​ ​ total​ ​ de​ ​ erros​ ​ 404.
    val total404Errors = lDs.filter($"http_reply_code" === 404).count()

    // Question 3. Os​ ​ 5 ​ ​ URLs​ ​ que​ ​ mais​ ​ causaram​ ​ erro​ ​ 404.
    val urls404ErrorsDF = lDs.filter($"http_reply_code" === 404).groupBy($"request").agg(count($"http_reply_code").alias("http_count")).orderBy($"http_count".desc)

    // Question 4. Quantidade​ ​ de​ ​ erros​ ​ 404​ ​ por​ ​ dia.
    val qty404ErrorsByDay = lDs.filter($"http_reply_code" === 404).groupBy(to_date($"collected_at").as("day")).agg(count($"collected_at").alias("total_errors")).orderBy($"day".desc)

    // Question 5. O​ ​ total​ ​ de​ ​ bytes​ ​ retornados.
    val totalBytes = lDs.filter($"reply_bytes".isNotNull).select("reply_bytes").rdd.map(_(0).asInstanceOf[Long]).reduce(_+_)

    // Printing answers
    println(s"Número de hosts únicos: ${hostsUnicos}")

    println(s"Total de erros 404: ${total404Errors}")

    println(s"Os 5 URLs que mais causaram erro 404: ")
    urls404ErrorsDF.show(5, false)

    println(s"Quantidade de erros 404 por dia: ")
    qty404ErrorsByDay.show(100, false)

    println(s"Total de Bytes retornados: ${totalBytes}")

    spark.stop()
  }
}