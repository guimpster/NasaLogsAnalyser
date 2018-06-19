# NasaLogsAnalyser
This is a simple spark application for reading nasa logs and the answer to the questions using:
  <ftp://ita.ee.lbl.gov/traces/NASA_access_log_Jul95.gz>:
  
```
Número de hosts únicos: 81983
Total de erros 404: 10845
Os 5 URLs que mais causaram erro 404: 
+--------------------------------------------------------+----------+
|request                                                 |http_count|
+--------------------------------------------------------+----------+
|GET /pub/winvn/readme.txt HTTP/1.0                      |667       |
|GET /pub/winvn/release.txt HTTP/1.0                     |547       |
|GET /history/apollo/apollo-13.html HTTP/1.0             |286       |
|GET /history/apollo/a-001/a-001-patch-small.gif HTTP/1.0|230       |
|GET /shuttle/resources/orbiters/atlantis.gif HTTP/1.0   |230       |
+--------------------------------------------------------+----------+
only showing top 5 rows

Quantidade de erros 404 por dia: 
+----------+------------+
|day       |total_errors|
+----------+------------+
|1995-07-28|105         |
|1995-07-27|330         |
|1995-07-26|344         |
|1995-07-25|471         |
|1995-07-24|318         |
|1995-07-23|231         |
|1995-07-22|191         |
|1995-07-21|340         |
|1995-07-20|419         |
|1995-07-19|647         |
|1995-07-18|465         |
|1995-07-17|420         |
|1995-07-16|238         |
|1995-07-15|252         |
|1995-07-14|417         |
|1995-07-13|532         |
|1995-07-12|464         |
|1995-07-11|493         |
|1995-07-10|379         |
|1995-07-09|348         |
|1995-07-08|317         |
|1995-07-07|579         |
|1995-07-06|657         |
|1995-07-05|475         |
|1995-07-04|369         |
|1995-07-03|465         |
|1995-07-02|276         |
|1995-07-01|303         |
+----------+------------+

Total de Bytes retornados: 38695973491
```
Obs: This another file <ftp://ita.ee.lbl.gov/traces/NASA_access_log_Aug95.gz> is protected, 
  therefore impossible to use spark.
 
### Exams answers (in portuguese):
    Qual o objetivo do comando cache em Spark?
        O .cache() ou .persist(MEMORY_ONLY) salvam resultados parciais dos RDD's em memória
        para que toda vez que um passo intermediário é executado, o spark não precise fazer
        toda a computação iterativa novamente, somente a partir do ponto onde foi cacheado.
        Todas as transformações do Spark são lazy, ou seja, o código só é executado quando
        houver uma ação(reduce por exemplo) ou cache()/persist().

    O mesmo código implementado em Spark é normalmente mais rápido que a implementação equivalente
    em MapReduce. Por quê?
        Porque o MapReduce precisa persistir e ler de discos(HDFS) a cada iteração, enquanto o Spark pode
        trabalhar os resultados diretamente da memória. Ou seja, Spark é ótimo para algoritmos
        iterativos que precisam ser rodados várias veres ou para aplicações que precisam fazer
        streaming de dados várias e várias vezes.

    Qual é a função do SparkContext?
        SparkContext é o conector de um cluster Spark, é usado para criar RDD's, acumuladores
        e fazer broadcast de variáveis pelo cluster. Só pode existir 1 SparkContext por JVM.

    Explique com suas palavras o que é Resilient Distributed Datasets(RDD).
        Resilient Distributed Datasets(RDD's) é uma abstração de cada grupo de dados distruido
        pelo cluster Spark. Cada RDD pode rodar operações em paralelo, criando novos RDD's.
        Assim, numa operação de leitura, eles são distribuidos nas memórias dos Clusters, realizando
        a transformação somente quando houver um cache() ou uma action como reduce().

    GroupByKey é menos eficiente que reduceByKey em grandes dataset. Por quê?
        Porque o GroupByKey transfere todos os dados que tem a mesma chave para o mesmo nó do cluster,
        além disso durante essa transferência se houver um grande grupo de (chave,valor), o spark persiste
        em disco o resultado, deixando todo o processo mais lento ainda.

    Explique o que o código Scala abaixo faz.
        linha 1: manda o SparkContext abrir um conector para um sistema HDFS(sistema de arquivos compartilhados)
        linha 2: cria uma coleção com todas as palavras de todas as linhas
        linha 3: cada palavra será uma tupla (palavra, 1)
        linha 4: soma chaves iguais
        linha 5: grava através do SparkContext de volta no HDFS

        Ou seja, a aplicação é um contador de palavras de um arquivo no HDFS
