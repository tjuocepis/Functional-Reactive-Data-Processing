import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.cases.{Location, User}
import com.cs474.server.stream.flows.{GenericDataFlows, UserDataFlows}

/**
  * Created by titusjuocepis on 12/4/16.
  */
class CsvToUserFlowTest extends TestKit(ActorSystemContainer.getInstance().getSystem) {

  implicit val materializer = ActorSystemContainer.getInstance().getMaterializer

  // Testing Flow csvToUser

  val usersDataLines = scala.io.Source.fromFile("data/BX-Users-TEST.csv", "ISO-8859-1").getLines().drop(1)
  val usersSource = Source.fromIterator(() => usersDataLines)

  usersSource.via(UserDataFlows.csvToUserFlow).runWith(TestSink.probe[User]).request(5)
    .expectNext(User("1", Location("nyc", "new york", "usa"), 0))
    .expectNext(User("2", Location("stockton", "california", "usa"), 18))
    .expectNext(User("3", Location("moscow","yukon territory","russia"), 0))
    .expectNext(User("4", Location("porto","v.n.gaia","portugal"), 17))
    .expectNext(User("5", Location("stockton","california","usa"), 26))
}
