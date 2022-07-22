package com.databricks.labs.mosaic.core.raster.api

import org.apache.spark.sql.catalyst.InternalRow

import com.databricks.labs.mosaic.core.raster.{MosaicRaster, MosaicRasterGDAL, RasterReader}

abstract class RasterAPI(reader: RasterReader) extends Serializable {

    def name: String
    def raster(input: Array[Byte]): MosaicRaster = reader.fromBytes(input)
    def raster(inputData: InternalRow): MosaicRaster = reader.fromBytes(inputData.getBinary(0))
    def raster(inputData: Any): MosaicRaster = reader.fromBytes(inputData.asInstanceOf[Array[Byte]])
    def raster(inputData: Any, inputPath: Any): MosaicRaster =
        reader.fromBytes(inputData.asInstanceOf[Array[Byte]], inputPath.asInstanceOf[String])

}

object RasterAPI extends Serializable {

    def apply(name: String): RasterAPI =
        name match {
            case "GDAL" => GDAL
        }

    def getReader(name: String): RasterReader =
        name match {
            case "GDAL" => MosaicRasterGDAL
        }

    object GDAL extends RasterAPI(MosaicRasterGDAL) {

        override def name: String = "GDAL"

    }

}
