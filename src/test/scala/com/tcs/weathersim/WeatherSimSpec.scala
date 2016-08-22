package com.tcs.weathersim

import com.tcs.weathersim.model.SimulationReq
import com.tcs.weathersim.model.canonical._
import com.tcs.weathersim.util.{RepeatableSelector, Selector}
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}

class WeatherSimSpec extends FlatSpec with Matchers {
  implicit val selector: Selector = RepeatableSelector
  val config = ConfigFactory.load
  val sim = new WeatherSim(config)

  "WeatherSim" should "get Simulation" in {
    val s = sim.getSimulations(SimulationReq(Latitude(-33.86), Longitude(151.21), new DateTime(0)))
    s.location shouldBe Some(Location("Sydney"))
    s.condition shouldBe Sunny
    s.pressure shouldBe Pressure(300)
    s.humidity shouldBe Humidity(32)
    s.temperature.self should equal (5.0 +- 1.0)
  }
}
