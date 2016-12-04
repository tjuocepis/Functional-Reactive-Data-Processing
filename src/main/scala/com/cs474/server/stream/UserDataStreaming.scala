package com.cs474.server.stream

import java.io.File

import akka.stream._
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Source, _}
import com.cs474.server.cases.{User, UserBookRating}
import com.cs474.server.stream.flows.DataFlows
import com.cs474.server.stream.sinks.{UserDataSinks, UserRatingsDataSinks}
import com.cs474.server.actor.ActorSystemContainer

import scala.util.Try
/**
  * Created by titusjuocepis on 12/1/16.
  *
  * Followed Akka Documentation from: http://doc.akka.io/docs/akka/2.4/scala/com.titus.stream/com.titus.stream-graphs.html
  */
class UserDataStreaming(usersFile: File, userRatingsData: File) {

  implicit val system = ActorSystemContainer.getInstance().getSystem
  implicit val materializer = ActorSystemContainer.getInstance().getMaterializer

  val usersDataLines = scala.io.Source.fromFile(usersFile, "ISO-8859-1").getLines().drop(1)
  val userRatingsDataLines = scala.io.Source.fromFile(userRatingsData, "ISO-8859-1").getLines().drop(1)

  def analyzeCityLocationData(city: String) = {
    val graph = RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder =>

        // SOURCES
        val usersSource = builder.add(Source.fromIterator(() => usersDataLines)).out

        // FLOWS
        val stringToUserFlowShape: FlowShape[String, User] =
        builder.add(DataFlows.csvToUser)

        val averageAgeFlowShape: FlowShape[User, (Int, Double, Int)] =
          builder.add(DataFlows.averageFlow)

        val averageAgePerAttributeFlowShape: FlowShape[User, (String, Int, Double, Int)] =
          builder.add(DataFlows.averagePerAttributeFlow)

        val filterByLocationFlowShape: FlowShape[User, User] =
          builder.add(DataFlows.filterByAttributeFlow(city))

        // BRANCHING
        val usersFanOutShape: UniformFanOutShape[User, User] = builder.add(Broadcast[User](2))

        // SINKS
        val averageUserAgeSink = builder.add(Sink.foreach(UserDataSinks.printAvgUserAgeForAllLocations)).in
        val averageUserAgeForLocationSink = builder.add(Sink.foreach(UserDataSinks.printAvgUserAgeForSpecifcLocation)).in

        // GRAPH STRUCTURE
        usersSource ~> stringToUserFlowShape ~> usersFanOutShape
        usersFanOutShape ~> averageAgeFlowShape ~> averageUserAgeSink
        usersFanOutShape ~> filterByLocationFlowShape ~> averageAgePerAttributeFlowShape ~> averageUserAgeForLocationSink

        // CLOSE
        ClosedShape

    }).run()
  }

  def analyzeUserData(user: String) = {
    val graph = RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder =>

        // SOURCES
        val ratingsSource = builder.add(Source.fromIterator(() => userRatingsDataLines)).out

        // FLOWS
        val stringToUserBookRatingFlowShape: FlowShape[String, UserBookRating] =
        builder.add(DataFlows.csvToUserBookRating)

        val averageRatingFlowShape: FlowShape[UserBookRating, (Int, Double, Int)] =
          builder.add(DataFlows.averageFlow)

        val averageRatingPerAttributeFlowShape: FlowShape[UserBookRating, (String, Int, Double, Int)] =
          builder.add(DataFlows.averagePerAttributeFlow)

        val filterByUserIdFlowShape: FlowShape[UserBookRating, UserBookRating] =
          builder.add(DataFlows.filterByAttributeFlow(user))

        val userBookRatingsToSeqFlowShape: FlowShape[UserBookRating, Seq[UserBookRating]] =
          builder.add(DataFlows.userBookRatingsToSeq)

        // BRANCHING
        val ratingsFanOutShape: UniformFanOutShape[UserBookRating, UserBookRating] = builder.add(Broadcast[UserBookRating](2))
        val filteredUsersFanOutShape: UniformFanOutShape[UserBookRating, UserBookRating] = builder.add(Broadcast[UserBookRating](2))

        // SINKS
        val averageUserRatingsDataSink = builder.add(Sink.foreach(UserRatingsDataSinks.printAllUsersDataAnalysis)).in
        val averageRatingForUserSink = builder.add(Sink.foreach(UserRatingsDataSinks.printSpecifUserDataAnalysis)).in
        val printSpecifcUserRatedBooksSink = builder.add(Sink.foreach(UserRatingsDataSinks.printSpecificUserRatedBooks)).in

        // GRAPH STRUCTURE (ratings)
        ratingsSource ~> stringToUserBookRatingFlowShape ~> ratingsFanOutShape
        ratingsFanOutShape ~> filterByUserIdFlowShape ~> filteredUsersFanOutShape
        ratingsFanOutShape ~> averageRatingFlowShape ~> averageUserRatingsDataSink
        filteredUsersFanOutShape ~> averageRatingPerAttributeFlowShape ~> averageRatingForUserSink
        filteredUsersFanOutShape ~> userBookRatingsToSeqFlowShape ~> printSpecifcUserRatedBooksSink

        // CLOSE
        ClosedShape

    }).run()
  }

  def startStreaming() = {

    val graph = RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder =>

        // SOURCES
        val usersSource = builder.add(Source.fromIterator(() => usersDataLines)).out
        val ratingsSource = builder.add(Source.fromIterator(() => userRatingsDataLines)).out

        // FLOWS
        val stringToUserFlowShape: FlowShape[String, User] =
          builder.add(DataFlows.csvToUser)

        val stringToUserBookRatingFlowShape: FlowShape[String, UserBookRating] =
          builder.add(DataFlows.csvToUserBookRating)

        val averageAgeFlowShape: FlowShape[Any, (Int, Double, Int)] =
          builder.add(DataFlows.averageFlow)

        val averageAgePerAttributeFlowShape: FlowShape[Any, (String, Int, Double, Int)] =
          builder.add(DataFlows.averagePerAttributeFlow)

        val averageRatingFlowShape: FlowShape[Any, (Int, Double, Int)] =
          builder.add(DataFlows.averageFlow)

        val averageRatingPerAttributeFlowShape: FlowShape[Any, (String, Int, Double, Int)] =
          builder.add(DataFlows.averagePerAttributeFlow)

        val filterByLocationFlowShape: FlowShape[Any, Any] =
          builder.add(DataFlows.filterByAttributeFlow("minneapolis, minnesota, usa"))

        val filterByUserIdFlowShape: FlowShape[UserBookRating, UserBookRating] =
          builder.add(DataFlows.filterByAttributeFlow("239872"))

        val userBookRatingsToSeqFlowShape: FlowShape[UserBookRating, Seq[UserBookRating]] =
          builder.add(DataFlows.userBookRatingsToSeq)

        // BRANCHING
        val usersFanOutShape: UniformFanOutShape[Any, Any] = builder.add(Broadcast[Any](2))
        val ratingsFanOutShape: UniformFanOutShape[UserBookRating, UserBookRating] = builder.add(Broadcast[UserBookRating](2))
        val filteredUsersFanOutShape: UniformFanOutShape[UserBookRating, UserBookRating] = builder.add(Broadcast[UserBookRating](2))

        // SINKS
        val averageUserAgeSink = builder.add(Sink.foreach(UserDataSinks.printAvgUserAgeForAllLocations)).in
        val averageUserAgeForLocationSink = builder.add(Sink.foreach(UserDataSinks.printAvgUserAgeForSpecifcLocation)).in
        val averageUserRatingsDataSink = builder.add(Sink.foreach(UserRatingsDataSinks.printAllUsersDataAnalysis)).in
        val averageRatingForUserSink = builder.add(Sink.foreach(UserRatingsDataSinks.printSpecifUserDataAnalysis)).in
        val printSpecifcUserRatedBooksSink = builder.add(Sink.foreach(UserRatingsDataSinks.printSpecificUserRatedBooks)).in

        // GRAPH STRUCTURE (users)
        usersSource ~> stringToUserFlowShape ~> usersFanOutShape

        usersFanOutShape ~> averageAgeFlowShape ~> averageUserAgeSink

        usersFanOutShape ~> filterByLocationFlowShape ~> averageAgePerAttributeFlowShape ~> averageUserAgeForLocationSink

        // GRAPH STRUCTURE (ratings)
        ratingsSource ~> stringToUserBookRatingFlowShape ~> ratingsFanOutShape

        ratingsFanOutShape ~> filterByUserIdFlowShape ~> filteredUsersFanOutShape

        ratingsFanOutShape ~> averageRatingFlowShape ~> averageUserRatingsDataSink

        filteredUsersFanOutShape ~> averageRatingPerAttributeFlowShape ~> averageRatingForUserSink

        filteredUsersFanOutShape ~> userBookRatingsToSeqFlowShape ~> printSpecifcUserRatedBooksSink

        //CLOSE
        ClosedShape

    }).run()
  }
}