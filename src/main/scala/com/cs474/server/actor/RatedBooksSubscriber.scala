package com.cs474.server.actor

import akka.actor.{ActorLogging, ActorRef}
import akka.stream.actor.ActorSubscriberMessage.OnNext
import akka.stream.actor.{ActorSubscriber, WatermarkRequestStrategy}
import com.cs474.server.cases.{UserBookRating, UserRatingsAnalysis}

/**
  * Actor Subscriber where book data sinks to.  The data gets sent to the ResponseActor
  *
  * @param actorRef The original sender that waits for a response to be sent to it
  */
class RatedBooksSubscriber(actorRef: ActorRef) extends ActorSubscriber with ActorLogging {

  val system = ActorSystemContainer.actorSystem()

  override def receive: Receive = {

    /**
      * When rated books are received for specific user
      */
    case OnNext(list: Seq[UserBookRating]) =>
      var response = "\n===========================================================\n" +
        s"Rated Books For User: ${list.head.userId} From Best To Worst =>"
      list.sortWith(_.rating > _.rating) foreach { ratedBook =>
        response = response.concat(s"\n${ratedBook.toString}")
      }

      ResponseActorContainer.instance() ! UserRatingsAnalysis(response, actorRef)
  }

  override protected def requestStrategy = WatermarkRequestStrategy(1)
}
