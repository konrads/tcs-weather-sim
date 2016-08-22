package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical._
import com.tcs.weathersim.util.Selector

/**
  * Humidity depends on:
  * - elevation (the higher the drier)
  * - water/land (more humid over water)
  * - season (winter is more humid)
  * - hour of day, mornings most humid
  */
class HumidityService(minHumidity: Humidity, maxHumidity: Humidity, highestPoint: Elevation)(implicit selector: Selector) {
  def getHumidity(elev: Elevation, massType: MassType, season: Season, hourOfDay: Int): Humidity = {
    val elevWeight = elev.value match {
      case x if x <= 0 => 0.0
      case x => (highestPoint.value - elev.value)/highestPoint.value
    }
    val landMassWeight = massType match {
      case Land => 0.1
      case Water => 0.7
    }
    val hourWeight = hourOfDay match {
      case h if h > 3 && h <= 10 => 0.8
      case h if h >= 12 && h < 17 => 0.1
      case _ => 0.5
    }
    val humidity = selector.select(minHumidity.value, maxHumidity.value, elevWeight, landMassWeight, hourWeight)
    Humidity(humidity)
  }
}
