// Databricks notebook source
// MAGIC %run "./../setup/EnableMosaic"

// COMMAND ----------

import org.apache.spark.sql.functions._

// COMMAND ----------

val username = "milos.colic" 
val silver_data_location = s"Users/${username}/geospatial/workshop/data/silver"

// COMMAND ----------

val polygons1 = spark.read.format("delta").load(s"/${silver_data_location}/h3/neighbourhoods/random/dataset_1_decomposed")
display(polygons1)

// COMMAND ----------

display(
  polygons1.select(
    polyfill(col("wkb_polygon"), 10)
  )
)

// COMMAND ----------

display(
  polygons1.select(mosaic_explode(col("wkb_polygon"), lit(10)))
)

// COMMAND ----------

polygons1.createOrReplaceGlobalTempView("polygons1")

// COMMAND ----------

// MAGIC %sql
// MAGIC 
// MAGIC select mosaic_explode(wkb_polygon, 10)
// MAGIC from polygons1

// COMMAND ----------


