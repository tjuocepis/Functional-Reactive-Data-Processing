package com.cs474.server.cases

/**
  * Location case class that holds city, state and country data
  *
  * @param city City of user
  * @param state State of user
  * @param country Country of user
  */
case class Location(city: String, state: String, country: String) {

  /**
    * Overrides the toString method for printing location
    *
    * @return Formatted string
    */
  override def toString: String = s"$city, $state, $country"

  /**
    * Checks to see if the city, state and country matches
    *
    * @param city City of user
    * @param state State of user
    * @param country Country of user
    * @return True or False depending if city, state and country matched
    */
  def isCity(city: String, state: String, country: String): Boolean = {
    if (this.city.contentEquals(city) && this.state.contentEquals(state) && this.country.contentEquals(country))
      true else false
  }

  /**
    * Checks to see if the location matches based on full string
    *
    * @param location Full location string
    * @return True or False depending if location matched
    */
  def isCity(location: String): Boolean = {
    if (toString.contentEquals(location)) true else false
  }

  /**
    * Checks to see if city and country match when state is not provided
    *
    * @param city City of user
    * @param country Country of user
    * @return True or False depending if city and country matched
    */
  def isStatelessCity(city: String, country: String): Boolean = {
    if (this.city.contentEquals(city) && this.country.contentEquals(country)) true else false
  }

  /**
    * Checks to see if state and country match parameters
    *
    * @param state State of user
    * @param country Country of user
    * @return True or False depending if state and country matched
    */
  def isState(state: String, country: String): Boolean = {
    if (this.state.contentEquals(state) && this.country.contentEquals(country)) true else false
  }

  /**
    * Checks to see if country matches
    *
    * @param country Country of user
    * @return True or False depending if country matched
    */
  def isCountry(country: String): Boolean = {
    if (this.country.contentEquals(country)) true else false
  }
}
