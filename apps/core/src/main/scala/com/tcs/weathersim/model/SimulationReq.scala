package com.tcs.weathersim.model

import com.tcs.weathersim.model.canonical.{Latitude, Longitude}
import com.tcs.weathersim.util.CSVCodec
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import scala.util.Try

/**
  * Represents a Simulation request. Provides means to read the Simulation in CSV format:
  * Latitude,Longitude,iso8601Date
  */
case class SimulationReq(lat: Latitude, long: Longitude, dt: DateTime)

object SimulationReq {
  implicit val csvCodec = new CSVCodec[SimulationReq] {
    val dateFmt = ISODateTimeFormat.dateTimeNoMillis

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