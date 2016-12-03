package com.cs474.stream

import java.io.File

import akka.stream.{ClosedShape, FlowShape, UniformFanOutShape}
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Source, _}
import com.cs474.actor.ActorSystemContainer
import com.cs474.cases.{User, UserBookRating}
import com.cs474.stream.flows.DataFlows
import com.cs474.stream.sinks.UserRatingsDataSinks

/**
  * Created by titusjuocepis on 12/2/16.
  */
class UserRatingsDataStreaming(userRatingsData: File) {

  implicit val system = ActorSystemContainer.getInstance().getSystem
  implicit val materializer = ActorSystemContainer.getInstance().getMaterializer

  val userRatingsDataLines = scala.io.Source.fromFile(userRatingsData, "ISO-8859-1").getLines().drop(1)

  def startStreaming() = {

    val graph = RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder =>

        // SOURCE
        val userRatingsDataSource = builder.add(Source.fromIterator(() => userRatingsDataLines)).out

        // FLOWS
        val stringToUserBookRatingFlowShape: FlowShape[String, UserBookRating] =
        builder.add(DataFlows.csvToUserBookRating)

        val averageUserBookRatingFlowShape: FlowShape[UserBookRating, (Int, Double, Int)] =
          builder.add(DataFlows.averageFlow)

        val averageBookRatingPerUserFlowShape: FlowShape[UserBookRating, (String, Int, Double, Int)] =
          builder.add(DataFlows.averagePerAttributeFlow)

        val filterByUserFlowShape: FlowShape[UserBookRating, UserBookRating] =
          builder.add(DataFlows.filterByAttributeFlow("239872"))

        // BRANCHING
        val fanOutShape: UniformFanOutShape[UserBookRating, UserBookRating] = builder.add(Broadcast[UserBookRating](2))

        // SINKS
        val averageUserRatingsDataSink = builder.add(Sink.foreach(UserRatingsDataSinks.averageUsersRating)).in
        val averageRatingPerUser = builder.add(Sink.foreach(UserRatingsDataSinks.averageRatingPerUser)).in

        // GRAPH STRUCTURE

        userRatingsDataSource ~> stringToUserBookRatingFlowShape ~> fanOutShape

        fanOutShape ~> averageUserBookRatingFlowShape ~> averageUserRatingsDataSink

        fanOutShape ~> filterByUserFlowShape ~> averageBookRatingPerUserFlowShape ~> averageRatingPerUser

        ClosedShape
    })

    graph.run()
  }
}