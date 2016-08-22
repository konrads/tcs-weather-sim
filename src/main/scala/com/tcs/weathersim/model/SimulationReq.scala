package com.tcs.weathersim.model

import com.tcs.weathersim.model.canonical.{Latitude, Longitude}
import com.tcs.weathersim.util.CSVCodec
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.util.Try

/**
  * Represents a Simulation request.
  */
case class SimulationReq(lat: Latitude, long: Longitude, dt: DateTime)

object SimulationReq {
  implicit val csvCoded = new CSVCodec[SimulationReq] {
    val dateFmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ")

    override def decode: PartialFunction[List[String], Try[SimulationReq]] = {
      case lat :: long :: dt :: Nil =>
        Try {
          SimulationReq(
            Latitude(lat.toDouble),
            Longitude(long.toDouble),
            dateFmt.parseDateTime(dt)
          )
        }
    }
  }
}