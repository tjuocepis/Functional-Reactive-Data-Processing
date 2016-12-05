package com.cs474.server.cases

/**
  * User case class that holds id, location and age data
  *
  * @param id Id of user
  * @param location Location of user
  * @param age Age of user
  */
case class User(id: String, location: Location, age: Int) {

  /**
    * Overrides toString method for printing user info
    *
    * @return
    */
  override def toString: String = s"User: $id from ${location.toString} age of $age"
}
