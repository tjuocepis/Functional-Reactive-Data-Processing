import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.cases.{Location, User}
import com.cs474.server.stream.flows.{GenericDataFlows, UserDataFlows}

/**
  * Created by titusjuocepis on 12/4/16.
  */
class AnalyzeAllUserDataFlowTest extends TestKit(ActorSystemContainer.actorSystem()) {

  implicit val materializer = ActorSystemContainer.materializer()

  // Testing analyzeDataFlow for collecting all users' data analysis

  val usersDataLines = scala.io.Source.fromFile("data/BX-Users-TEST.csv", "ISO-8859-1").getLines().drop(1)
  val usersSource = Source.fromIterator(() => usersDataLines)

  usersSource.via(UserDataFlows.csvToUserFlow).via(GenericDataFlows.analyzeDataFlow).runWith(TestSink.probe[(Int, Double, Int)]).request(1)
    .expectNext((3, 61.0, 5))
}
