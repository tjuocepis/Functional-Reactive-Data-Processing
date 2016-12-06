package com.cs474.server.stream.sinks

import java.text.DecimalFormat

import com.cs474.server.cases.UserBookRating

import scala.util.Try

/**
  * Instance that defines methods for printing user ratings data
  */
object UserRatingsDataSinks {

  val df = new DecimalFormat("#.##")

  def printAllUsersDataAnalysis[usersSource](source: usersSource) {
    var analysis = s"===========================================================\n"
    source match {
      case (userRatingsCount: Int, ratingsSum: Double, totalRatingsCount: Int) =>
        analysis = analysis.concat(s"For all users => \n" +
                                   s"Total number of ratings is: $totalRatingsCount \n" +
                                   s"Average rating is: ${Try(df.format(ratingsSum/userRatingsCount)).getOrElse(0)} " +
                                   s"| ($ratingsSum / $userRatingsCount) \n" +
                                   s"Number of data entries with provided rating: $userRatingsCount \n" +
                                   s"Number of data entries with rating being 0 (0 means not rated):" +
                                   s" ${totalRatingsCount-userRatingsCount}")
        println(analysis)

      case bad => println(s"Bad case: $bad")

      case _ => println("No data was found")
    }
  }

  /**
    * Prints out the processed results of average rating per specific user id that comes from the stream
    *
    * @param source Processed stream of results
    * @tparam usersSource Processed stream of results
    */
  def printSpecifUserDataAnalysis[usersSource](source: usersSource) {
    var analysis = s"===========================================================\n"
    source match {
      case (userId: String, userRatingsCount: Int, ratingsSum: Double, totalRatings: Int) =>
        analysis = analysis.concat(s"For the user id: $userId => \n" +
                                   s"Total number of ratings is: $totalRatings \n" +
                                   s"Average rating is: ${Try(df.format(ratingsSum/userRatingsCount)).getOrElse(0)} " +
                                   s"| ($ratingsSum / $userRatingsCount) \n" +
                                   s"Number of data entries with provided ratings: $userRatingsCount \n" +
                                   s"Number of data entries with rating being 0 (0 means not rated):" +
                                   s" ${totalRatings-userRatingsCount}")
        println(analysis)

      case bad => println(s"Bad case: $bad")

      case _ => println(s"No data found for the user")
    }
  }

  /**
    * Prints out a specific user's rated books
    *
    * @param source Processed stream of results
    * @tparam usersSource Processed stream of results
    */
  def printSpecificUserRatedBooks[usersSource](source: usersSource) = {
    source match {
      case list: Seq[UserBookRating] =>
        println("===========================================================")
        println(s"Rated Books For User: ${list.head.userId} From Best To Worst =>")
        list.sortWith(_.rating > _.rating) foreach { ratedBook =>
          println(ratedBook.toString)
        }
    }
  }
}
