import java.io.File

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape, FlowShape, Graph}
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source}
import com.cs474.cases.User
import com.cs474.stream.flows.DataFlows
import com.cs474.stream.sinks.UserDataSinks
import akka.testkit
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import com.cs474.stream.flows.DataFlows
import org.scalatest.enablers.KeyMapping
import akka.stream.scaladsl._
import akka.stream.{FlowShape, Graph, Materializer, UniformFanOutShape}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.stream._


/**
  * Created by titusjuocepis on 12/3/16.
  */
class CsvToUserFlowTest extends TestKit(ActorSystem("CsvToUserFlowTest"))
                        with WordSpecLike
                        with Matchers
                        with BeforeAndAfterAll {

/*

  implicit val materializer = ActorMaterializer()

  object CsvToUserFlowMock extends DataFlows
  val user = User("2", "nyc, new york, usa", "18")

  "CsvToUser Flow" should {

    "Have user in filtered output" in withMessage(user) { message =>

      println("2OPPSODFSKDOPF")
      val temp = graph(CsvToUserFlowMock.csvToUser.shape, message)

      println("3OPPSODFSKDOPF")
      val result = Await.result(temp, 1000.millis)

      implicit val keyMapping = KeyMapping

      println(">>>>>>>>>>> " + result.asInstanceOf[User].id)

      result.asInstanceOf[User].id should contain ("2")
    }
  }

  def withMessage(user: User)(testCode: User => Unit) = {
    testCode(user)
  }

  def graph[User](flow: FlowShape[String, User], message: User): Future[Any] = {
    graph(flow, Source.single(message))
  }

  /*
  test("Testing averageUserAge Sink") {
    val usersDataLines = scala.io.Source.fromFile(new File("data/BX-Users-Tiny.csv"), "ISO-8859-1").getLines().drop(1)
    val usersSource = Source.fromIterator(() => usersDataLines)
    val csvToUserFlow = DataFlows.csvToUser
    val averageAgeFlow = csvToUserFlow.via(DataFlows.averageFlow)
    val sinkUnderTest = Sink.foreach(UserDataSinks.averageUserAge)

    val future = usersSource.via(averageAgeFlow).runWith(sinkUnderTest)

    val result = Await.result(future, 3.seconds)

    assert(result == 20)
  }
  */
  */
}
