package com.databricks.labs.mosaic.expressions.raster

import com.databricks.labs.mosaic.core.raster.operator.merge.MergeRasters
import com.databricks.labs.mosaic.core.types.RasterTileType
import com.databricks.labs.mosaic.core.types.model.MosaicRasterTile
import com.databricks.labs.mosaic.expressions.base.{GenericExpressionFactory, WithExpressionInfo}
import com.databricks.labs.mosaic.expressions.raster.base.RasterArrayExpression
import com.databricks.labs.mosaic.functions.MosaicExpressionConfig
import org.apache.spark.sql.catalyst.analysis.FunctionRegistry.FunctionBuilder
import org.apache.spark.sql.catalyst.expressions.codegen.CodegenFallback
import org.apache.spark.sql.catalyst.expressions.{Expression, NullIntolerant}

/** Returns a raster that is a result of merging an array of rasters. */
case class RST_Merge(
    rastersExpr: Expression,
    expressionConfig: MosaicExpressionConfig
) extends RasterArrayExpression[RST_Merge](
      rastersExpr,
      RasterTileType(expressionConfig.getCellIdType),
      returnsRaster = true,
      expressionConfig = expressionConfig
    )
      with NullIntolerant
      with CodegenFallback {

    /**
      * Merges an array of rasters.
      * @param tiles
      *   The rasters to be used.
      * @return
      *   The merged raster.
      */
    override def rasterTransform(tiles: Seq[MosaicRasterTile]): Any = {
        val index = if (tiles.map(_.getIndex).groupBy(identity).size == 1) tiles.head.getIndex else null
        tiles.head.copy(
          raster = MergeRasters.merge(tiles.map(_.getRaster)),
          index = index
        )
    }

}

/** Expression info required for the expression registration for spark SQL. */
object RST_Merge extends WithExpressionInfo {

    override def name: String = "rst_merge"

    override def usage: String =
        """
          |_FUNC_(expr1) - Returns a raster that is a result of merging an array of rasters.
          |""".stripMargin

    override def example: String =
        """
          |    Examples:
          |      > SELECT _FUNC_(array(raster_tile_1, raster_tile_2, raster_tile_3));
          |        {index_id, raster, parent_path, driver}
          |        {index_id, raster, parent_path, driver}
          |        ...
          |  """.stripMargin

    override def builder(expressionConfig: MosaicExpressionConfig): FunctionBuilder = {
        GenericExpressionFactory.getBaseBuilder[RST_Merge](1, expressionConfig)
    }

}
