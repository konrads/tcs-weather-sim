package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical._
import com.tcs.weathersim.util.{RepeatableSelector, Selector}
import org.scalatest.{FlatSpec, Matchers}

class HumidityServiceSpec extends FlatSpec with Matchers {
  implicit val selector: Selector = RepeatableSelector
  val service = new HumidityService(Humidity(10), Humidity(70), Elevation(1000))

  "HumidityService" should "get low humidity" in {
    service.getHumidity(Elevation(900), Land, Summer, 12) shouldBe Humidity(16)
  }

  it should "get high humidity" in {
    service.getHumidity(Elevation(100), Water, Winter, 6) shouldBe Humidity(58)
  }
}
