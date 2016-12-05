import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.stream.flows.{GenericDataFlows, UserDataFlows}

/**
  * Created by titusjuocepis on 12/5/16.
  */
class AnalyzeUserDataForLocationFlowTest extends TestKit(ActorSystemContainer.getInstance().getSystem) {

  implicit val materializer = ActorSystemContainer.getInstance().getMaterializer

  // Testing filterByAttributeFlow AND analyzeDataForAttributeFlow for collecting specific users' data analysis

  val usersDataLines = scala.io.Source.fromFile("data/BX-Users-TEST.csv", "ISO-8859-1").getLines().drop(1)
  val usersSource = Source.fromIterator(() => usersDataLines)

  usersSource.via(UserDataFlows.csvToUserFlow).via(GenericDataFlows.filterByAttributeFlow("stockton, california, usa")).via(GenericDataFlows.analyzeDataForAttributeFlow).runWith(TestSink.probe[(String,Int,Double,Int)]).request(1)
    .expectNext(("stockton, california, usa",2,44.0,2))
}
