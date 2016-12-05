import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.cases.UserBookRating
import com.cs474.server.stream.flows.{BookRatingsDataFlows, GenericDataFlows}

/**
  * Created by titusjuocepis on 12/5/16.
  */
class FilterUserRatingsDataByUserFlowTest extends TestKit(ActorSystemContainer.getInstance().getSystem) {

  implicit val materializer = ActorSystemContainer.getInstance().getMaterializer

  // Testing filterByAttributeFlow AND analyzeDataForAttributeFlow for collecting specific users' data analysis

  val ratingsDataLines = scala.io.Source.fromFile("data/BX-Book-Ratings-TEST.csv", "ISO-8859-1").getLines().drop(1)
  val usersSource = Source.fromIterator(() => ratingsDataLines)

  usersSource.via(BookRatingsDataFlows.csvToUserBookRatingFlow).via(GenericDataFlows.filterByAttributeFlow("2")).runWith(TestSink.probe[UserBookRating]).request(2)
    .expectNext(UserBookRating("2", "0155061224", 5))
    .expectNext(UserBookRating("2", "0521795028", 6))
}