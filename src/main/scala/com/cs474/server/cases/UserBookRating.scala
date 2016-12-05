package com.cs474.server.cases

/**
  * UserBookRating case class that holds userId, bookISBN and rating data
  *
  * @param userId Id of user
  * @param bookISBN Isbn of rated book
  * @param rating Rating of book
  */
case class UserBookRating(userId: String, bookISBN: String, rating: Int) {

  /**
    * Overrides toString method for printing user book rating
    *
    * @return
    */
  override def toString: String = s"Book Rating: ISBN = $bookISBN rating = $rating"
}
