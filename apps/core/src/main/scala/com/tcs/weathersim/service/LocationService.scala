package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical.{LocationBoundary, Latitude, Location, Longitude}
import com.typesafe.config.Config

import scala.collection.JavaConverters._

/**
  * Location (ie. well known label) defined in config.
  */
class LocationService(config: Config) {
  val locationBoundaries = config.getObjectList("locations").asScala.map(lc => LocationBoundary.parse(lc.toConfig))

  def getLocation(lat: Latitude, long: Longitude): Option[Location] =
    locationBoundaries.collectFirst {
      case LocationBoundary(loc, minLat, minLong, maxLat, maxLong)
        if minLat.value <= lat.value && minLong.value <= long.value && maxLat.value > lat.value && maxLong.value > long.value => loc
    }
}
