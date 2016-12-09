package com.cs474.server.actor

import java.text.DecimalFormat
import akka.actor.{ActorLogging, ActorRef}
import akka.stream.actor.ActorSubscriberMessage.{OnComplete, OnError, OnNext}
import akka.stream.actor.{ActorSubscriber, WatermarkRequestStrategy}
import com.cs474.server.cases.{LocationAnalysis, UserRatingsAnalysis}
import scala.util.Try

/**
  * Actor Subscriber where analyzed user ratings data sinks to.  The analyzed data gets sent to the ResponseActor
  *
  * @param actorRef The original sender that waits for a response to be sent to it
  */
class UserRatingsDataStreamSubscriber (actorRef: ActorRef) extends ActorSubscriber with ActorLogging {

  val system = ActorSystemContainer.actorSystem()
  val df = new DecimalFormat("#.##") // Formats Double values

  override def receive: Receive = {

    /**
      * When all user ratings data analysis is received
      */
    case OnNext((usersWithRating: Int, ratingsSum: Double, totalRatingsCount: Int)) =>
      val response = s"\n===========================================================\n" +
        s"For all users => \n" +
        s"Total number of ratings is: $totalRatingsCount \n" +
        s"Average rating is: ${Try(df.format(ratingsSum/usersWithRating)).getOrElse(0)} " +
        s"| ($ratingsSum / $usersWithRating) \n" +
        s"Number of data entries with provided rating: $usersWithRating \n" +
        s"Number of data entries with rating being 0 (0 means not rated):" +
        s" ${totalRatingsCount-usersWithRating}"

      ResponseActorContainer.instance() ! UserRatingsAnalysis(response, actorRef)

    /**
      * When user ratings data analysis is received for specific user
      */
    case OnNext((userId: String, usersWithRating: Int, ratingsSum: Double, totalRatings: Int)) =>
      val response = s"\n===========================================================\n" +
        s"For the user id: $userId => \n" +
        s"Total number of ratings is: $totalRatings \n" +
        s"Average rating is: ${Try(df.format(ratingsSum/usersWithRating)).getOrElse(0)} " +
        s"| ($ratingsSum / $usersWithRating) \n" +
        s"Number of data entries with provided ratings: $usersWithRating \n" +
        s"Number of data entries with rating being 0 (0 means not rated):" +
        s" ${totalRatings-usersWithRating}"

      ResponseActorContainer.instance() ! UserRatingsAnalysis(response, actorRef)
  }

  override protected def requestStrategy = WatermarkRequestStrategy(1)
}
