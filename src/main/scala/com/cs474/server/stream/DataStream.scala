package com.cs474.server.stream

import java.io.File

import akka.actor.{ActorRef, Props}
import akka.stream._
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Source, _}
import com.cs474.server.cases.{User, UserBookRating}
import com.cs474.server.stream.flows.{BookRatingsDataFlows, GenericDataFlows, UserDataFlows}
import com.cs474.server.stream.sinks.{UserDataSinks, UserRatingsDataSinks}
import com.cs474.server.actor.{ActorSystemContainer, LocationDataStreamSubscriber, RatedBooksSubscriber, UserRatingsDataStreamSubscriber}

/**
  * Defines methods for streaming data and analyzing data from different sources
  *
  * Followed Akka Documentation from: http://doc.akka.io/docs/akka/2.4/scala/com.titus.stream/com.titus.stream-graphs.html
  */
class DataStream {

  implicit val system = ActorSystemContainer.getInstance().getSystem
  implicit val materializer = ActorSystemContainer.getInstance().getMaterializer

  /**
    * Starts a stream to analyze data for specific location as well as all locations
    *
    * @param location Location to analyze data for
    */
  def analyzeLocationData(location: String, actorRef: ActorRef) = {

    val usersDataLines = scala.io.Source.fromFile("data/BX-Users.csv", "ISO-8859-1").getLines().drop(1)

    val graph = RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder =>

        // SOURCES
        val usersSource = builder.add(Source.fromIterator(() => usersDataLines)).out

        // FLOWS
        val stringToUserFlowShape: FlowShape[String, User] =
        builder.add(UserDataFlows.csvToUserFlow)

        val averageAgeFlowShape: FlowShape[User, (Int, Double, Int)] =
          builder.add(GenericDataFlows.analyzeDataFlow)

        val averageAgePerAttributeFlowShape: FlowShape[User, (String, Int, Double, Int)] =
          builder.add(GenericDataFlows.analyzeDataForAttributeFlow)

        val filterByLocationFlowShape: FlowShape[User, User] =
          builder.add(GenericDataFlows.filterByAttributeFlow(location))

        // BRANCHING
        val usersFanOutShape: UniformFanOutShape[User, User] = builder.add(Broadcast[User](2))

        // SINKS
        val averageUserAgeSink = builder.add(Sink.actorSubscriber(Props(new LocationDataStreamSubscriber(actorRef))))//builder.add(Sink.foreach(UserDataSinks.printAvgUserAgeForAllLocations)).in
        val averageUserAgeForLocationSink = builder.add(Sink.actorSubscriber(Props(new LocationDataStreamSubscriber(actorRef))))//builder.add(Sink.foreach(UserDataSinks.printAvgUserAgeForSpecifcLocation)).in


        // GRAPH STRUCTURE
        usersSource ~> stringToUserFlowShape ~> usersFanOutShape
        usersFanOutShape ~> averageAgeFlowShape ~> averageUserAgeSink
        usersFanOutShape ~> filterByLocationFlowShape ~> averageAgePerAttributeFlowShape ~> averageUserAgeForLocationSink

        // CLOSE
        ClosedShape

    }).run()
  }

  /**
    * Starts a stream to analyze data for specific user as well as all users
    *
    * @param user User to analyze data for
    */
  def analyzeUserData(user: String, actorRef: ActorRef) = {

    val userRatingsDataLines = scala.io.Source.fromFile("data/BX-Book-Ratings.csv", "ISO-8859-1").getLines().drop(1)

    val graph = RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder =>

        // SOURCES
        val ratingsSource = builder.add(Source.fromIterator(() => userRatingsDataLines)).out

        // FLOWS
        val stringToUserBookRatingFlowShape: FlowShape[String, UserBookRating] =
        builder.add(BookRatingsDataFlows.csvToUserBookRatingFlow)

        val averageRatingFlowShape: FlowShape[UserBookRating, (Int, Double, Int)] =
          builder.add(GenericDataFlows.analyzeDataFlow)

        val averageRatingPerAttributeFlowShape: FlowShape[UserBookRating, (String, Int, Double, Int)] =
          builder.add(GenericDataFlows.analyzeDataForAttributeFlow)

        val filterByUserIdFlowShape: FlowShape[UserBookRating, UserBookRating] =
          builder.add(GenericDataFlows.filterByAttributeFlow(user))

        val userBookRatingsToSeqFlowShape: FlowShape[UserBookRating, Seq[UserBookRating]] =
          builder.add(BookRatingsDataFlows.userBookRatingsToSeq)

        // BRANCHING
        val ratingsFanOutShape: UniformFanOutShape[UserBookRating, UserBookRating] = builder.add(Broadcast[UserBookRating](2))
        val filteredUsersFanOutShape: UniformFanOutShape[UserBookRating, UserBookRating] = builder.add(Broadcast[UserBookRating](2))

        // SINKS
        val averageUserRatingsDataSink = builder.add(Sink.actorSubscriber(Props(new UserRatingsDataStreamSubscriber(actorRef))))//builder.add(Sink.foreach(UserRatingsDataSinks.printAllUsersDataAnalysis)).in
        val averageRatingForUserSink = builder.add(Sink.actorSubscriber(Props(new UserRatingsDataStreamSubscriber(actorRef))))//builder.add(Sink.foreach(UserRatingsDataSinks.printSpecifUserDataAnalysis)).in
        val specifcUserRatedBooksSink = builder.add(Sink.actorSubscriber(Props(new RatedBooksSubscriber(actorRef))))//builder.add(Sink.foreach(UserRatingsDataSinks.printSpecificUserRatedBooks)).in

        // GRAPH STRUCTURE (ratings)
        ratingsSource ~> stringToUserBookRatingFlowShape ~> ratingsFanOutShape
        ratingsFanOutShape ~> filterByUserIdFlowShape ~> filteredUsersFanOutShape
        ratingsFanOutShape ~> averageRatingFlowShape ~> averageUserRatingsDataSink
        filteredUsersFanOutShape ~> averageRatingPerAttributeFlowShape ~> averageRatingForUserSink
        filteredUsersFanOutShape ~> userBookRatingsToSeqFlowShape ~> specifcUserRatedBooksSink

        // CLOSE
        ClosedShape

    }).run()
  }
}