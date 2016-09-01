package com.tcs.weathersim.model

import cats.data.{NonEmptyList => NEL}
import cats.std.all._
import cats.{Semigroup, _}
import com.tcs.weathersim.model.canonical._
import com.tcs.weathersim.util.PSVCodec
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

/**
  * Represents the Simulation output.
  */
case class Simulation(location: Option[Location], lat: Latitude, long: Longitude,
                      elevation: Elevation, dateTime: DateTime, condition: Condition,
                      temperature: Temperature, pressure: Pressure, humidity: Humidity)

object Simulation {
  implicit val psvCodec = new PSVCodec[Simulation] {
    val dateFmt = ISODateTimeFormat.dateTimeNoMillis.withZoneUTC()

    override def toElems(s: Simulation): Seq[String] =
      Seq(
        s.location.map(_.value).getOrElse(""),
        "%.2f,%.2f,%.2f".format(s.lat.value, s.long.value, s.elevation.value),
        dateFmt.print(s.dateTime),
        s.condition.toString,
        "%.2f".format(s.temperature.value),
        "%.2f".format(s.pressure.value),
        "%d".format(s.humidity.value)
      )
  }

  implicit val nelSemigroup: Semigroup[NEL[Simulation]] = SemigroupK[NEL].algebra[Simulation]
}
