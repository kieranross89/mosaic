package com.databricks.labs.mosaic.expressions.geometry

import com.databricks.labs.mosaic.core.geometry.api.JTS
import com.databricks.labs.mosaic.core.index.{BNGIndexSystem, H3IndexSystem}
import org.apache.spark.sql.QueryTest
import org.apache.spark.sql.catalyst.expressions.CodegenObjectFactoryMode
import org.apache.spark.sql.internal.SQLConf
import org.apache.spark.sql.test.SharedSparkSession

class ST_HaversineTest extends QueryTest with SharedSparkSession with ST_HaversineBehaviors {

    private val noCodegen =
        withSQLConf(
          SQLConf.WHOLESTAGE_CODEGEN_ENABLED.key -> "false",
          SQLConf.CODEGEN_FACTORY_MODE.key -> CodegenObjectFactoryMode.NO_CODEGEN.toString
        ) _

    private val codegenOnly =
        withSQLConf(
          SQLConf.ADAPTIVE_EXECUTION_ENABLED.key -> "false",
          SQLConf.WHOLESTAGE_CODEGEN_ENABLED.key -> "true",
          SQLConf.CODEGEN_FACTORY_MODE.key -> CodegenObjectFactoryMode.CODEGEN_ONLY.toString
        ) _

    test("Testing stHaversine (H3, JTS) NO_CODEGEN") { noCodegen { haversineBehaviour(H3IndexSystem, JTS) } }
    test("Testing stHaversine (BNG, JTS) NO_CODEGEN") { noCodegen { haversineBehaviour(BNGIndexSystem, JTS) } }
    test("Testing stHaversine (H3, JTS) CODEGEN compilation") { codegenOnly { haversineCodegen(H3IndexSystem, JTS) } }
    test("Testing stHaversine (BNG, JTS) CODEGEN compilation") { codegenOnly { haversineCodegen(BNGIndexSystem, JTS) } }
    test("Testing stHaversine (H3, JTS) CODEGEN_ONLY") { codegenOnly { haversineBehaviour(H3IndexSystem, JTS) } }
    test("Testing stHaversine (BNG, JTS) CODEGEN_ONLY") { codegenOnly { haversineBehaviour(BNGIndexSystem, JTS) } }
    test("Testing stRotate auxiliaryMethods (H3, JTS)") { noCodegen { auxiliaryMethods(H3IndexSystem, JTS) } }
    test("Testing stRotate auxiliaryMethods (BNG, JTS)") { noCodegen { auxiliaryMethods(BNGIndexSystem, JTS) } }

}
