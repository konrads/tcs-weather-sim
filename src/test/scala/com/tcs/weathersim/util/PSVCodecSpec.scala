package com.tcs.weathersim.util

import com.tcs.weathersim.model.Simulation
import com.tcs.weathersim.model.canonical._
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}

class PSVCodecSpec extends FlatSpec with Matchers {
  "PSVCodec" should "encode simulation" in {
    val s = Simulation(Some(Location("Atlantis")), Latitude(2.2), Longitude(1.1), Elevation(12.12), new DateTime(0), Sunny, Temperature(34.34), Pressure(45.45), Humidity(78))
    PSVCodec.encode(s) shouldEqual "Atlantis|2.20,1.10,12.12|1970-01-01T00:00:00Z|Sunny|34.34|45.45|78"
  }
}
