package com.tcs.weathersim.model.canonical

import com.typesafe.config.Config

/**
  * Helper class for definition of Seasonal boundaries.
  */
case class LocationBoundary(loc: Location, minLat: Latitude, minLong: Longitude, maxLat: Latitude, maxLong: Longitude) {
  assert(minLat.value < maxLat.value, s"location: ${loc.value}: min lat > max lat")
  assert(minLong.value < maxLong.value, s"location: ${loc.value}: min long > max long")
}

object LocationBoundary {
  def parse(config: Config) =
    LocationBoundary(
      loc     = Location(config.getString("name")),
      minLat  = Latitude(config.getDouble("min_lat")),
      minLong = Longitude(config.getDouble("min_long")),
      maxLat  = Latitude(config.getDouble("max_lat")),
      maxLong = Longitude(config.getDouble("max_long")))
}
