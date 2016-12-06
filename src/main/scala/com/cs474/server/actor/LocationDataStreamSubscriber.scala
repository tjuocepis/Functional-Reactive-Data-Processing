package com.cs474.server.actor

import java.text.DecimalFormat
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.stream.actor.ActorSubscriberMessage.OnNext
import akka.stream.actor.{ActorSubscriber, WatermarkRequestStrategy}
import com.cs474.server.cases.LocationAnalysis
import scala.util.Try

/**
  * Actor Subscriber where analyzed location data sinks to.  The analyzed data gets sent to the ResponseActor
  *
  * @param actorRef The original sender that waits for a response to be sent to it
  */
class LocationDataStreamSubscriber(actorRef: ActorRef) extends ActorSubscriber with ActorLogging {

  val system = ActorSystemContainer.getInstance().getSystem
  val df = new DecimalFormat("#") // Formats Double values

  override def receive: Receive = {

    /**
      * When all locations data analysis is received
      */
    case OnNext((usersWithAgeCount: Int, ageSum: Double, totalUsersCount: Int)) =>
      val response = s"\n===========================================================\n" +
        s"For all locations => \n" +
        s"Total number of readers is: $totalUsersCount \n" +
        s"Average reader age is: ${df.format(Try(ageSum / usersWithAgeCount).getOrElse(0))} " +
        s"| ($ageSum / $usersWithAgeCount) \n" +
        s"Number of data entries with provided age: $usersWithAgeCount \n" +
        s"Number of data entries with age being NULL: ${totalUsersCount-usersWithAgeCount}"

        ResponseActorContainer.instance() ! LocationAnalysis(response, actorRef)

    /**
      * When location analysis is received for specific location
      */
    case OnNext((location: String, usersWithAgeCount: Int, ageSum: Double, totalUsersCount: Int)) =>
      val response = s"\n===========================================================\n" +
        s"For the location: $location => \n" +
        s"Total number of readers is: $totalUsersCount \n" +
        s"Average reader age is: ${df.format(Try(ageSum/usersWithAgeCount).getOrElse(0))} " +
        s"| ($ageSum / $usersWithAgeCount) \n" +
        s"Number of data entries with provided age: $usersWithAgeCount \n" +
        s"Number of data entries with age being NULL: ${totalUsersCount-usersWithAgeCount}"


        ResponseActorContainer.instance() ! LocationAnalysis(response, actorRef)
  }

  override protected def requestStrategy = WatermarkRequestStrategy(1)
}
