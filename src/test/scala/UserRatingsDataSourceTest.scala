import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.cs474.server.actor.ActorSystemContainer

/**
  * Created by titusjuocepis on 12/5/16.
  */
class UserRatingsDataSourceTest extends TestKit(ActorSystemContainer.actorSystem()) {

  implicit val materializer = ActorSystemContainer.materializer()

  // Testing Source from BX-Users-TEST.csv

  val ratingsDataLines = scala.io.Source.fromFile("data/BX-Book-Ratings-TEST.csv", "ISO-8859-1").getLines().drop(1)
  val usersSource = Source.fromIterator(() => ratingsDataLines)

  usersSource.runWith(TestSink.probe[String]).request(5)
    .expectNext(""""1";"034545104X";"0"""")
    .expectNext(""""2";"0155061224";"5"""")
    .expectNext(""""3";"0446520802";"0"""")
    .expectNext(""""4";"052165615X";"3"""")
    .expectNext(""""2";"0521795028";"6"""")
}
