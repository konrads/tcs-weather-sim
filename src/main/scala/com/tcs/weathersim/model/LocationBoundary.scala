package com.tcs.weathersim.model

import com.tcs.weathersim.model.canonical.{Longitude, Latitude, Location}
import com.typesafe.config.Config

case class LocationBoundary(loc: Location, minLat: Latitude, minLong: Longitude, maxLat: Latitude, maxLong: Longitude) {
  assert(minLat.self < maxLat.self, s"location: ${loc.self}: min lat > max lat")
  assert(minLong.self < maxLong.self, s"location: ${loc.self}: min long > max long")
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
