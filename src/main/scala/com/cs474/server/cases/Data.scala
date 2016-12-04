package com.cs474.server.cases

/**
  * Created by titusjuocepis on 12/3/16.
  */
trait Data

/**
  * User case class that holds id, location and age data
  *
  * @param id
  * @param location
  * @param age
  */
case class User(id: String, location: Location, age: String) extends Data {

  override def toString: String = s"User: $id from ${location.toString} age of $age"
}

/**
  *
  * @param isbn
  * @param title
  * @param author
  * @param year
  * @param publisher
  */
case class Book(isbn: String, title: String, author: String, year: String, publisher: String) extends Data

/**
  * UserBookRating case class that holds userId, bookISBN and rating data
  *
  * @param userId
  * @param bookISBN
  * @param rating
  */
case class UserBookRating(userId: String, bookISBN: String, rating: Int) extends Data {

  override def toString: String = s"Book Rating: ISBN = $bookISBN rating = $rating"
}
