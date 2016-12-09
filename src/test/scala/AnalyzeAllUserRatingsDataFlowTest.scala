import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.stream.flows.{BookRatingsDataFlows, GenericDataFlows}

/**
  * Created by titusjuocepis on 12/5/16.
  */
class AnalyzeAllUserRatingsDataFlowTest extends TestKit(ActorSystemContainer.actorSystem()) {

  implicit val materializer = ActorSystemContainer.materializer()

  // Testing filterByAttributeFlow AND analyzeDataForAttributeFlow for collecting specific users' data analysis

  val ratingsDataLines = scala.io.Source.fromFile("data/BX-Book-Ratings-TEST.csv", "ISO-8859-1").getLines().drop(1)
  val usersSource = Source.fromIterator(() => ratingsDataLines)

  usersSource.via(BookRatingsDataFlows.csvToUserBookRatingFlow).via(GenericDataFlows.analyzeDataFlow).runWith(TestSink.probe[(Int,Double,Int)]).request(1)
    .expectNext((3,14.0,5))
}
