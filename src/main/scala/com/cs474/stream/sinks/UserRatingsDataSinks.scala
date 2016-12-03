package com.cs474.stream.sinks

import java.text.DecimalFormat

import scala.util.Try

/**
  * Created by titusjuocepis on 12/2/16.
  */
object UserRatingsDataSinks {

  val df = new DecimalFormat("#.##")

  def averageUsersRating[usersSource](source: usersSource) {
    source match {
      case (userRatingsCount: Int, ratingsSum: Double, totalRatingsCount: Int) =>
        println(s"===========================================================\n" +
                s"For all users => \n" +
                s"Total number of ratings is: $totalRatingsCount \n" +
                s"Average rating is: ${Try(df.format(ratingsSum/userRatingsCount)).getOrElse(0)} | ($ratingsSum / $userRatingsCount) \n" +
                s"Number of data entries with provided rating: $userRatingsCount \n" +
                s"Number of data entries with rating being 0 (0 means not rated): ${totalRatingsCount-userRatingsCount}")
      case bad =>
        println(s"Bad case: $bad")
    }
  }

  /**
    * Prints out the processed results of average rating per specific user id that comes from the stream
    *
    * @param source processed stream of results
    * @tparam usersSource processed stream of results
    */
  def averageRatingPerUser[usersSource](source: usersSource): Unit = {
    source match {
      case (userId: String, userRatingsCount: Int, ratingsSum: Double, totalRatings: Int) =>
        println(s"===========================================================\n" +
          s"For the user id: $userId => \n" +
          s"Total number of ratings is: $totalRatings \n" +
          s"Average rating is: ${Try(df.format(ratingsSum/userRatingsCount)).getOrElse(0)} | ($ratingsSum / $userRatingsCount) \n" +
          s"Number of data entries with provided ratings: $userRatingsCount \n" +
          s"Number of data entries with rating being 0 (0 means not rated): ${totalRatings-userRatingsCount}")
      case bad => println(s"Bad case: $bad")
    }
  }
}
