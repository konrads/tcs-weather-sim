package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical._
import com.tcs.weathersim.util.{RepeatableSelector, Selector}
import org.scalatest.{FlatSpec, Matchers}

class PressureServiceSpec extends FlatSpec with Matchers {
  implicit val selector: Selector = RepeatableSelector
  val service = new PressureService(300, 1000, 1000)

  "PressureService" should "get low pressure" in {
    service.getPressure(Elevation(900)) shouldBe Pressure(370)
  }

  it should "get high pressure" in {
    service.getPressure(Elevation(100)) shouldBe Pressure(930)
  }
}
