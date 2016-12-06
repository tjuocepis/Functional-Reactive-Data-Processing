package com.cs474.server.stream.sinks

import java.text.DecimalFormat
import scala.util.Try

/**
  * Holds all the functions needed for the Sinks to process BX-Users.csv
  */
object UserDataSinks {

  val df = new DecimalFormat("#")

  /**
    * Prints out the processed results of average user age for the entire data set that comes from the stream
    *
    * @param source processed stream of results
    * @tparam usersSource processed stream of results
    */
  def printAvgUserAgeForAllLocations[usersSource](source: usersSource) {
    source match {
      case (usersWithAgeCount: Int, ageSum: Double, totalUsersCount: Int) =>
        val response = s"===========================================================\n" +
                       s"For all locations => \n" +
                       s"Total number of readers is: $totalUsersCount \n" +
                       s"Average reader age is: ${df.format(Try(ageSum/usersWithAgeCount).getOrElse(0))} " +
                       s"| ($ageSum / $usersWithAgeCount) \n" +
                       s"Number of data entries with provided age: $usersWithAgeCount \n" +
                       s"Number of data entries with age being NULL: ${totalUsersCount-usersWithAgeCount}"
        println(response)

      case bad => println(s"Bad case: $bad")
    }
  }

  /**
    * Prints out the processed results of average user age per specific location that comes from the stream
    *
    * @param source processed stream of results
    * @tparam usersSource processed stream of results
    */
  def printAvgUserAgeForSpecifcLocation[usersSource](source: usersSource): Unit = {
    source match {
      case (location: String, usersWithAgeCount: Int, ageSum: Double, totalUsersCount: Int) =>
        val response = s"===========================================================\n" +
                       s"For the location: $location => \n" +
                       s"Total number of readers is: $totalUsersCount \n" +
                       s"Average reader age is: ${df.format(Try(ageSum/usersWithAgeCount).getOrElse(0))} " +
                       s"| ($ageSum / $usersWithAgeCount) \n" +
                       s"Number of data entries with provided age: $usersWithAgeCount \n" +
                       s"Number of data entries with age being NULL: ${totalUsersCount-usersWithAgeCount}"
        println(response)

      case bad => println(s"Bad case: $bad")
    }
  }
}