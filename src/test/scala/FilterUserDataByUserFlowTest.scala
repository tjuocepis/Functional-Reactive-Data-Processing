import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.cases.{Location, User}
import com.cs474.server.stream.flows.{GenericDataFlows, UserDataFlows}

/**
  * Created by titusjuocepis on 12/5/16.
  */
class FilterUserDataByUserFlowTest extends TestKit(ActorSystemContainer.actorSystem()) {

  implicit val materializer = ActorSystemContainer.materializer()

  // Testing filterByAttributeFlow AND analyzeDataForAttributeFlow for collecting specific users' data analysis

  val usersDataLines = scala.io.Source.fromFile("data/BX-Users-TEST.csv", "ISO-8859-1").getLines().drop(1)
  val usersSource = Source.fromIterator(() => usersDataLines)

  usersSource.via(UserDataFlows.csvToUserFlow).via(GenericDataFlows.filterByAttributeFlow("stockton, california, usa")).runWith(TestSink.probe[User]).request(2)
    .expectNext(User("2", Location("stockton", "california", "usa"), 18))
    .expectNext(User("5", Location("stockton", "california", "usa"), 26))
}
