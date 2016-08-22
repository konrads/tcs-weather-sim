package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical.{Latitude, Location, LocationBoundary, Longitude}

/**
  * Location (ie. well known label) is defined in config.
  * Mocked up service in this instance, in reality would be far more complex.
  */
class LocationService(locationBoundaries: Seq[LocationBoundary]) {
  def getLocation(lat: Latitude, long: Longitude): Option[Location] =
    locationBoundaries.collectFirst {
      case LocationBoundary(loc, minLat, minLong, maxLat, maxLong)
        if minLat.value <= lat.value && minLong.value <= long.value && maxLat.value > lat.value && maxLong.value > long.value => loc
    }
}
