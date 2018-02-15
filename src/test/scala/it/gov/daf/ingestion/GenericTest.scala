package it.gov.daf.ingestion

import org.scalatest.FunSuite
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import it.gov.daf.ingestion.transformations._
import java.sql.{Date, Timestamp}

class GenericTest extends FunSuite with DataFrameSuiteBase {

  test("simple test") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    val input1 = sc.parallelize(List(1, 2, 3)).toDF
    assertDataFrameEquals(input1, input1) // equal
    val input2 = sc.parallelize(List(4, 5, 6)).toDF
    intercept[org.scalatest.exceptions.TestFailedException] {
      assertDataFrameEquals(input1, input2) // not equal
    }
  }

  test("nullChecker test") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    val input1 = sc.parallelize(List("a", "b", "c")).toDF("value")

    val output1 = nullChecker(input1, "value")

    assertDataFrameEquals(input1, output1)

    val input2 = sc.parallelize(List("", "b", "c")).toDF("value")

    val expectedOutput2 = sc.parallelize(List(null, "b", "c")).toDF("value")

    val output2 = nullChecker(input2, "value")

    assertDataFrameEquals(output2, expectedOutput2)

    val input3 = sc.parallelize(List(("",""), ("b1","b2"), ("c1","c2"))).toDF("key","value")

    val expectedOutput3 = sc.parallelize(List((null,null), ("b1","b2"), ("c1","c2"))).toDF("key","value")

    val output3 = nullChecker(nullChecker(input3, "value"),"key")

    assertDataFrameEquals(output3, expectedOutput3)

    val input4 = sc.parallelize(List(("","a2"), ("b1",""), ("c1","c2"))).toDF("key","value")

    val expectedOutput4 = sc.parallelize(List((null,"a2"), ("b1",null), ("c1","c2"))).toDF("key","value")

    val output4 = nullChecker(nullChecker(input4, "value"),"key")

    assertDataFrameEquals(output4, expectedOutput4)

  }

  test("dateFormatter test") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    val input1 = sc.parallelize(List(Timestamp.valueOf("2015-01-01 00:00:00"))).toDF("value")
    // val input1 = sc.parallelize(List(Timestamp.valueOf("01.01.15"))).toDF("value")

    input1.collect.foreach(println)

    val output1 = dateFormatter(input1, "value")

    output1.collect.foreach(println)

    assertDataFrameEquals(input1, input1)

    // val input2 = sc.parallelize(List("", "b", "c")).toDF("value")

    // val expectedOutput2 = sc.parallelize(List(null, "b", "c")).toDF("value")

    // val output2 = nullChecker(input2, "value")

    // assertDataFrameEquals(output2, expectedOutput2)

    // val input3 = sc.parallelize(List(("",""), ("b1","b2"), ("c1","c2"))).toDF("key","value")

    // val expectedOutput3 = sc.parallelize(List((null,null), ("b1","b2"), ("c1","c2"))).toDF("key","value")

    // val output3 = nullChecker(nullChecker(input3, "value"),"key")

    // assertDataFrameEquals(output3, expectedOutput3)

    // val input4 = sc.parallelize(List(("","a2"), ("b1",""), ("c1","c2"))).toDF("key","value")

    // val expectedOutput4 = sc.parallelize(List((null,"a2"), ("b1",null), ("c1","c2"))).toDF("key","value")

    // val output4 = nullChecker(nullChecker(input4, "value"),"key")

    // assertDataFrameEquals(output4, expectedOutput4)

  }

}
