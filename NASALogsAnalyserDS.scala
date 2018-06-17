// Best references:
//      http://xinhstechblog.blogspot.com/2016/07/overview-of-spark-20-dataset-dataframe.html
//      https://www.balabit.com/blog/spark-scala-dataset-tutorial

import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

val LOG_REGEX = """^(.*) - - \[(.*)\].*\"(.*) (.*) (.*)\" (\d*) ([-|\d]*)$"""
val DATE_FORMAT = """dd/MMM/yyyy:HH:mm:ss Z"""

case class Log(host: String, collected_at: java.sql.Timestamp, method: String, url: String, http_version: String, http_reply_code: Integer, reply_bytes: Long) {
    override def toString(): String = "(" + host + ", " + collected_at + ", " + method + ", " + url + ", " + http_version + ", " + http_reply_code + ", " + reply_bytes + ")";
}

val logsDF = sc.textFile("/media/guilherme/backup/Documentos 2018/Semantix/access_log_Jul95").toDF

val lDs = logsDF.select(regexp_extract($"value", LOG_REGEX, 1) as "host",
    unix_timestamp(regexp_extract($"value", LOG_REGEX, 2), DATE_FORMAT).cast(TimestampType) as "collected_at",
    regexp_extract($"value", LOG_REGEX, 3) as "method",
    regexp_extract($"value", LOG_REGEX, 4) as "url",
    regexp_extract($"value", LOG_REGEX, 5) as "http_version",
    regexp_extract($"value", LOG_REGEX, 6).cast("Int") as "http_reply_code",
    when(regexp_extract($"value", LOG_REGEX, 7).isNotNull, regexp_extract($"value", LOG_REGEX, 7).cast("Int")).otherwise(lit(0)) as "reply_bytes").as[Log]

// Question 1. Número​ ​ de​ ​ hosts​ ​ únicos.
val hostsUnicos = lDs.filter($"host".isNotNull).select("host").distinct.count()

// Question 2. O​ ​ total​ ​ de​ ​ erros​ ​ 404.
val total404Errors = lDs.filter($"http_reply_code" === 404).count()

// Question 3. Os​ ​ 5 ​ ​ URLs​ ​ que​ ​ mais​ ​ causaram​ ​ erro​ ​ 404.
val urls404ErrorsDF = lDs.filter($"http_reply_code" === 404).groupBy($"url").agg(count($"http_reply_code").alias("http_count")).orderBy($"http_count".desc)

// Question 4. Quantidade​ ​ de​ ​ erros​ ​ 404​ ​ por​ ​ dia.
val qty404ErrorsByDay = lDs.filter($"http_reply_code" === 404).groupBy(to_date($"collected_at").as("day")).agg(count($"collected_at").alias("total_errors")).orderBy($"day".desc)

// Question 5. O​ ​ total​ ​ de​ ​ bytes​ ​ retornados.
val totalBytes = lDs.filter($"reply_bytes".isNotNull).map(_.reply_bytes).reduce(_ + _)


// Printing answers
println(s"Número de hosts únicos: ${hostsUnicos}")

println(s"Total de erros 404: ${total404Errors}")

println(s"Os 5 URLs que mais causaram erro 404: ")
urls404ErrorsDF.show(5, false)

println(s"Quantidade de erros 404 por dia: ")
qty404ErrorsByDay.show(100, false)

println(s"Total de Bytes retornados: ${totalBytes}")
