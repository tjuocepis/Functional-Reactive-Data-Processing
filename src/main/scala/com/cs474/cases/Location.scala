package com.cs474.cases

/**
  * Created by titusjuocepis on 12/3/16.
  */
case class Location(city: String, state: String, country: String) {

  override def toString: String = s"$city, $state, $country"

  def isCity(city: String, state: String, country: String): Boolean = {
    if ((this.city.contentEquals(city) && this.state.contentEquals(state) && this.country.contentEquals(country)) ||
         this.city.contentEquals(city) && this.country.contentEquals(country)) true else false
  }

  def isState(state: String, country: String): Boolean = {
    if (this.state.contentEquals(state) && this.country.contentEquals(country)) true else false
  }

  def isCountry(country: String): Boolean = {
    if (this.country.contentEquals(country)) true else false
  }
}
