package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical.{LocationBoundary, Latitude, Location, Longitude}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

class LocationServiceSpec extends FlatSpec with Matchers {
  val service = new LocationService(Seq(
    LocationBoundary(Location("Sydney"),    Latitude(-34.1), Longitude(150.5), Latitude(-33.6), Longitude(151.5)),
    LocationBoundary(Location("Australia"), Latitude(-48.8), Longitude(111.3), Latitude(-10.8), Longitude(155.3)),
    LocationBoundary(Location("London"),    Latitude(51.3),  Longitude(-0.5),  Latitude(51.7),  Longitude(0.3)),
    LocationBoundary(Location("Europe"),    Latitude(34.3),  Longitude(-11.2), Latitude(71.7),  Longitude(51.7)),
    LocationBoundary(Location("Earth"),     Latitude(-90),  Longitude(-180),   Latitude(90),    Longitude(180))
  ))

  "LocationService" should "find Sydney" in {
    service.getLocation(Latitude(-33.9), Longitude(151.22)) shouldBe Some(Location("Sydney"))
  }

  it should "find Australia" in {
    service.getLocation(Latitude(-23), Longitude(134)) shouldBe Some(Location("Australia"))
  }

  it should "find London" in {
    service.getLocation(Latitude(51.4), Longitude(0)) shouldBe Some(Location("London"))
  }

  it should "find Europe" in {
    service.getLocation(Latitude(51.9), Longitude(21.1)) shouldBe Some(Location("Europe"))
  }

  it should "default to Earth" in {
    service.getLocation(Latitude(5.8), Longitude(22.5)) shouldBe Some(Location("Earth"))
  }
}
