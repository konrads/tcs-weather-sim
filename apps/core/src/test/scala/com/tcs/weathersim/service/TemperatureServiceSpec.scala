package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical._
import com.tcs.weathersim.util.{RepeatableSelector, Selector}
import org.scalatest.{FlatSpec, Matchers}

class TemperatureServiceSpec extends FlatSpec with Matchers {
  implicit val selector: Selector = RepeatableSelector
  val service = new TemperatureService(Temperature(-10), Temperature(40), Elevation(1000))

  "TemperatureServiceSpec" should "get low temperature" in {
    service.getTemperature(Latitude(80), Elevation(900), Land, 1, Winter).value should be < 0.0
  }

  it should "get high temperature" in {
    service.getTemperature(Latitude(1), Elevation(5), Land, 12, Summer).value should be > 30.0
  }
}
