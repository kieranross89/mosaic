package com.databricks.labs.mosaic.expressions.raster

import com.databricks.labs.mosaic.core.geometry.api.GeometryAPI
import com.databricks.labs.mosaic.core.index.IndexSystem
import com.databricks.labs.mosaic.functions.MosaicContext
import org.apache.spark.sql.QueryTest
import org.scalatest.matchers.should.Matchers._

trait RST_SkewXBehaviors extends QueryTest {

    def skewXBehavior(indexSystem: IndexSystem, geometryAPI: GeometryAPI): Unit = {
        spark.sparkContext.setLogLevel("FATAL")
        val mc = MosaicContext.build(indexSystem, geometryAPI)
        mc.register()
        val sc = spark
        import mc.functions._
        import sc.implicits._

        val rastersInMemory = spark.read
            .format("gdal")
            .option("raster_storage", "in-memory")
            .load("src/test/resources/binary/netcdf-coral")

        val df = rastersInMemory
            .withColumn("result", rst_skewx($"tile"))
            .select("result")

        rastersInMemory
            .createOrReplaceTempView("source")

        noException should be thrownBy spark.sql("""
                                                   |select rst_skewx(tile) from source
                                                   |""".stripMargin)

        noException should be thrownBy rastersInMemory
            .withColumn("result", rst_skewx($"tile"))
            .select("result")

        val result = df.as[Double].collect().max

        result == 0 shouldBe true

        an[Exception] should be thrownBy spark.sql("""
                                                     |select rst_skewx() from source
                                                     |""".stripMargin)

    }

}
