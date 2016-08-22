package com.tcs.weathersim.util

import com.tcs.weathersim.model.canonical._
import org.scalatest.{FlatSpec, Matchers}

class PNGGridSpec extends FlatSpec with Matchers {
  lazy val elevations = PNGGrid.readElevations("/gebco_08_rev_elev_21600x10800.png", 8848)
  lazy val waterLandMask = PNGGrid.readMassTypeMask("/water_16k.png")

  "PNGGrid" should "obtain reasonable elevations for Himalayas" in {
    val himalayas = elevations(Latitude(38.6), Longitude(75.2))
    himalayas.value should be > 7000.0
    himalayas.value should be < 9000.0
  }

  it should "obtain reasonable elevations for Andes" in {
    val andes = elevations(Latitude(-16.73), Longitude(-67.48))
    andes.value should be > 5000.0
    andes.value should be < 6000.0
  }

  it should "obtain reasonable elevations for Sydney" in {
    val sydney = elevations(Latitude(-33.9), Longitude(151.22))
    sydney.value should be >  0.0
    sydney.value should be < 50.0
  }

  it should "detect land in Sydney" in {
    val pacific = waterLandMask(Latitude(-33.9), Longitude(151.22))
    pacific shouldBe Land
  }

  it should "detect water in Botany Bay" in {
    val pacific = waterLandMask(Latitude(-34.98), Longitude(151.18))
    pacific shouldBe Water
  }
}
