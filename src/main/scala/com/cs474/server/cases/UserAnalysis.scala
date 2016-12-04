package com.cs474.server.cases

/**
  * Created by titusjuocepis on 12/3/16.
  */
case class UserAnalysis(userId: String, numOfRatings: Int, avgRating: Int,
                        bestBook: Option[Book], worstBook: Option[Book])
