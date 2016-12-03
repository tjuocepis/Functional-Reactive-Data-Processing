package com.cs474.stream

import java.io.File

import akka.stream._
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Source, _}
import com.cs474.cases.User
import com.cs474.stream.flows.DataFlows
import com.cs474.stream.sinks.UserDataSinks
import com.cs474.actor.ActorSystemContainer

import scala.util.Try
/**
  * Created by titusjuocepis on 12/1/16.
  *
  * Followed Akka Documentation from: http://doc.akka.io/docs/akka/2.4/scala/com.titus.stream/com.titus.stream-graphs.html
  */
class UserDataStreaming(usersFile: File) {

  implicit val system = ActorSystemContainer.getInstance().getSystem
  implicit val materializer = ActorSystemContainer.getInstance().getMaterializer

  val usersDataLines = scala.io.Source.fromFile(usersFile, "ISO-8859-1").getLines().drop(1)

  def startStreaming() = {

    val graph = RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder =>

        // SOURCE
        val usersSource = builder.add(Source.fromIterator(() => usersDataLines)).out

        // FLOWS
        val stringToUserFlowShape: FlowShape[String, User] =
          builder.add(DataFlows.csvToUser)

        val averageUserAgeFlowShape: FlowShape[Any, (Int, Double, Int)] =
          builder.add(DataFlows.averageFlow)

        val averageUserAgePerLocationFlowShape: FlowShape[Any, (String, Int, Double, Int)] =
          builder.add(DataFlows.averagePerAttributeFlow)

        val filterByLocationFlowShape: FlowShape[Any, Any] =
          builder.add(DataFlows.filterByAttributeFlow("minneapolis, minnesota, usa"))

        // BRANCHING
        val fanOutShape: UniformFanOutShape[Any, Any] = builder.add(Broadcast[Any](2))

        // SINKS
        val averageUserAgeSink = builder.add(Sink.foreach(UserDataSinks.averageUserAge)).in
        val averageUserAgePerLocationSink = builder.add(Sink.foreach(UserDataSinks.averageUserAgePerLocation)).in

        // GRAPH STRUCTURE
        usersSource ~> stringToUserFlowShape  ~> fanOutShape

        fanOutShape ~> filterByLocationFlowShape ~> averageUserAgePerLocationFlowShape ~> averageUserAgePerLocationSink

        fanOutShape ~> averageUserAgeFlowShape ~> averageUserAgeSink

        ClosedShape
    })

    graph.run()
  }
}