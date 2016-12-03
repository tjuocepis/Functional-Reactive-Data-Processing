package com.cs474.cases

/**
  * Created by titusjuocepis on 12/3/16.
  */
case class UserAnalysis(user: Option[User], numOfRatings: Option[Int], avgRating: Option[Int],
                        bestBook: Option[Book], worstBook: Option[Book])
