package com.tcs.weathersim.model.canonical

import org.joda.time.{DateTimeZone, DateTime}

/**
  * Helper for defining seasons of the year.
  */
sealed trait Season
case object Spring extends Season
case object Summer extends Season
case object Autumn extends Season
case object Winter extends Season

object Season {
  def get(dt: DateTime, lat: Latitude): Season = {
    import SeasonalBoundaries._
    val dayOfYear = dt.getDayOfYear
    val isNorthern = lat.value > 0
    if (dayOfYear < b1 || dayOfYear > b4)
      if (isNorthern) Winter else Summer
    else if (dayOfYear < b2)
      if (isNorthern) Spring else Autumn
    else if (dayOfYear < b3)
      if (isNorthern) Summer else Winter
    else if (isNorthern) Autumn else Spring
  }

  private object SeasonalBoundaries {
    private val anyDay = new DateTime(0).withZone(DateTimeZone.UTC)
    val b1 = anyDay.withMonthOfYear(3).getDayOfYear
    val b2 = anyDay.withMonthOfYear(6).getDayOfYear
    val b3 = anyDay.withMonthOfYear(9).getDayOfYear
    val b4 = anyDay.withMonthOfYear(12).getDayOfYear
  }
}
