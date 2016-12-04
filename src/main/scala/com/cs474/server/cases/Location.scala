package com.cs474.server.cases

/**
  * Created by titusjuocepis on 12/3/16.
  */
case class Location(city: String, state: String, country: String) {

  override def toString: String = s"$city, $state, $country"

  def isCity(city: String, state: String, country: String): Boolean = {
    if (this.city.contentEquals(city) && this.state.contentEquals(state) && this.country.contentEquals(country))
      true else false
  }

  def isCity(location: String): Boolean = {
    if (toString.contentEquals(location)) true else false
  }

  def isStatelessCity(city: String, country: String): Boolean = {
    if (this.city.contentEquals(city) && this.country.contentEquals(country)) true else false
  }

  def isState(state: String, country: String): Boolean = {
    if (this.state.contentEquals(state) && this.country.contentEquals(country)) true else false
  }

  def isCountry(country: String): Boolean = {
    if (this.country.contentEquals(country)) true else false
  }
}
