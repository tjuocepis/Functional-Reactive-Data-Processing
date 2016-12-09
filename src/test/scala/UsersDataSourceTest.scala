import akka.stream.scaladsl.Source
import akka.stream.testkit.TestSubscriber.OnSubscribe
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.cases.{Location, User}
import com.cs474.server.stream.flows.GenericDataFlows

/**
  * Created by titusjuocepis on 12/4/16.
  */
class UsersDataSourceTest extends TestKit(ActorSystemContainer.actorSystem()) {

  implicit val materializer = ActorSystemContainer.materializer()

  // Testing Source from BX-Users-TEST.csv

  val usersDataLines = scala.io.Source.fromFile("data/BX-Users-TEST.csv", "ISO-8859-1").getLines().drop(1)
  val usersSource = Source.fromIterator(() => usersDataLines)

  usersSource.runWith(TestSink.probe[String]).request(5)
    .expectNext(""""1";"nyc, new york, usa";NULL""")
    .expectNext(""""2";"stockton, california, usa";"18"""")
    .expectNext(""""3";"moscow, yukon territory, russia";NULL""")
    .expectNext(""""4";"porto, v.n.gaia, portugal";"17"""")
    .expectNext(""""5";"stockton, california, usa";"26"""")
}
