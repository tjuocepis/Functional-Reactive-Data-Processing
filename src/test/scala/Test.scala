import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.cs474.server.actor.ActorSystemContainer
import org.scalatest.FunSuite

import scala.concurrent.duration._

/**
  * Created by titusjuocepis on 12/4/16.
  */
class Test extends FunSuite {

  test("Testing Source streaming from BX-Users-TEST.csv") {
    val test = new UsersDataSourceTest
  }

  test("Testing csvToUser Flow for converting incoming data to User case class") {
    val test = new CsvToUserFlowTest
  }

  test("Testing analyzeData Flow for collecting all users' data analysis") {
    val test = new AnalyzeAllUserDataFlowTest
  }

  test("Testing filterByAttribute Flow for filtering all users' data for specific location") {
    val test = new FilterUserDataByUserFlowTest
  }

  test("Testing analyzeDataForAttribute Flow for collecting users' data for specific location") {
    val test = new AnalyzeUserDataForLocationFlowTest
  }

  test("Testing csvToUserBookRating Flow for converting incoming data to UserBookRating case class") {
    val test = new CsvToBookRatingFlowTest
  }

  test("Testing Source streaming from BX-Book-Ratings-TEST.csv") {
    val test = new UserRatingsDataSourceTest
  }

  test("Testing analyzeData Flow for collecting all users' ratings data analysis") {
    val test = new AnalyzeAllUserRatingsDataFlowTest
  }

  test("Testing filterByAttribute Flow for filtering all users' ratings data for specific user") {
    val test = new FilterUserRatingsDataByUserFlowTest
  }

  test("Testing analyzeDataForAttribute Flow for collecting specific user's ratings data analysis") {
    val test = new AnalyzeUserRatingsDataForUserFlowTest
  }
}
