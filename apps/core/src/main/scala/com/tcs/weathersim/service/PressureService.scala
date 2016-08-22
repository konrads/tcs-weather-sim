package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical.{Elevation, Pressure}
import com.tcs.weathersim.util.Selector

/**
  * Pressure depends on elevation.
  */
class PressureService(minPressure: Pressure, maxPressure: Pressure, highestPoint: Elevation)(implicit selector: Selector) {
  def getPressure(elev: Elevation): Pressure = {
    val elevWeight = elev.value match {
      case x if x <= 0 => 0
      case x => (highestPoint.value - elev.value)/highestPoint.value
    }
    val pressure = selector.select(minPressure.value, maxPressure.value, elevWeight)
    Pressure(pressure)
  }
}
