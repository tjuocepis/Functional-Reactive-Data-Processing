import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.cases.{Location, User, UserBookRating}
import com.cs474.server.stream.flows.{BookRatingsDataFlows, GenericDataFlows}

/**
  * Created by titusjuocepis on 12/5/16.
  */
class CsvToBookRatingFlowTest extends TestKit(ActorSystemContainer.actorSystem()) {

  implicit val materializer = ActorSystemContainer.materializer()

  // Testing Flow csvToUser

  val ratingsDataLines = scala.io.Source.fromFile("data/BX-Book-Ratings-TEST.csv", "ISO-8859-1").getLines().drop(1)
  val usersSource = Source.fromIterator(() => ratingsDataLines)

  usersSource.via(BookRatingsDataFlows.csvToUserBookRatingFlow).runWith(TestSink.probe[UserBookRating]).request(5)
    .expectNext(UserBookRating("1","034545104X",0))
    .expectNext(UserBookRating("2","0155061224",5))
    .expectNext(UserBookRating("3","0446520802",0))
    .expectNext(UserBookRating("4","052165615X",3))
    .expectNext(UserBookRating("2","0521795028",6))
}
