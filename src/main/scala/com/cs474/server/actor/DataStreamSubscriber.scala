package com.cs474.server.actor

import java.text.DecimalFormat

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.stream.actor.ActorSubscriberMessage.OnNext
import akka.stream.actor.{ActorSubscriber, RequestStrategy, WatermarkRequestStrategy}
import akka.stream.scaladsl.Sink
import com.cs474.server.cases.{LocationAnalysis, StartLocationAnalysis, StartUserAnalysis}
import com.cs474.server.stream.DataStream

import scala.util.Try

/**
  * Actor Subscriber where analyzed data sinks to.  The analyzed data gets sent to the ResponseActor
  *
  * @param actorRef The original sender that waits for a response to be sent to it
  */
class DataStreamSubscriber(actorRef: ActorRef) extends ActorSubscriber with ActorLogging {

  val system = ActorSystemContainer.getInstance().getSystem
  val df = new DecimalFormat("#") // Formats Double values

  override def receive: Receive = {

    /**
      * When user data analysis is received
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
      * When data analysis is received for specific location
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
