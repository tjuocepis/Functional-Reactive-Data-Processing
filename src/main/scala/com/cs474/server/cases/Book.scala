package com.cs474.server.cases

/**
  * Book case clss that holds isbn, title, author, year and publisher data
  *
  * @param isbn Isbn number of book
  * @param title Title of book
  * @param author Author of book
  * @param year Year of book
  * @param publisher Publisher of book
  */
case class Book(isbn: String, title: String, author: String, year: String, publisher: String) {

  /**
    * Overrides toString method for printing book info
    *
    * @return
    */
  override def toString: String = s"$title by $author published in $year by $publisher ISBN: $isbn"
}
