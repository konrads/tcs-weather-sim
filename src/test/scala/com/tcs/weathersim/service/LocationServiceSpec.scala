package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical.{Latitude, Location, Longitude}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

class LocationServiceSpec extends FlatSpec with Matchers {
  val service = new LocationService(ConfigFactory.load())

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
