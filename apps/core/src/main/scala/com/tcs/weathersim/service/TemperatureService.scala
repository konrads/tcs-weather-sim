package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical._
import com.tcs.weathersim.util.Selector

/**
  * Temperature depends on:
  * - latitude (higher if closer to equator)
  * - elevation (higher if closer to sea level)
  * - water/land mass (more extreme over land)
  * - hour of day, highest at noon
  * - season (highest at Summer, lowest at Winter)
  */
class TemperatureService(minTemp: Double, maxTemp: Double, highestPoint: Double)(implicit selector: Selector) {
  def getTemperature(lat: Latitude, elev: Elevation, massType: MassType, hourOfDay: Int, season: Season): Temperature = {
    val latWeight = 1.0 - Math.abs(lat.self)/90

    val elevWeight = elev.self match {
      case x if x <= 0 => 0.0
      case x => (highestPoint - elev.self)/highestPoint
    }

    val hourWeight = hourOfDay match {
      case h if h >= 12 && h < 17 => 1.0
      case h if h > 0 && h <= 7 => 0.1
      case _ => 0.5
    }

    val massTypeSeasonyWeight = (massType, season) match {
      case (Land, Summer) => 0.9
      case (Water, Summer) => 0.7
      case (Land, Winter) => 0.1
      case (Water, Winter) => 0.3
      case _ => 0.5
    }

    val temp = selector.select(minTemp, maxTemp, latWeight, elevWeight, hourWeight, massTypeSeasonyWeight)
    Temperature(temp)
  }
}
