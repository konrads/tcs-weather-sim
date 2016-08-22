package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical.{Elevation, Pressure}
import com.tcs.weathersim.util.Selector

/**
  * Pressure depends on elevation.
  */
class PressureService(minPressure: Double, maxPressure: Double, highestPoint: Double)(implicit selector: Selector) {
  def getPressure(elev: Elevation): Pressure = {
    val elevWeight = elev.self match {
      case x if x <= 0 => 0
      case x => (highestPoint - elev.self)/highestPoint
    }
    val pressure = selector.select(minPressure, maxPressure, elevWeight)
    Pressure(pressure)
  }
}
