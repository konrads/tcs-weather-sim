package com.tcs.weathersim.util

import cats.data.Validated.{invalidNel, valid}
import cats.data.{NonEmptyList => NEL, Validated}
import cats.std.all._
import cats.syntax.semigroup._
import cats._

import scala.util.{Failure, Success, Try}

/**
  * Codec for comma separated values.
  */
trait CSVCodec[T] {
  def decode: PartialFunction[List[String], Try[T]]
}

object CSVCodec {
  def decode[T: CSVCodec](line: String): Validated[NEL[CSVParseErr], NEL[T]] = {
    val decoder = implicitly[CSVCodec[T]]
    val asSeq = line.split(",").toList.map(_.trim)
    if (decoder.decode.isDefinedAt(asSeq))
      decoder.decode(asSeq) match {
        case Success(t) => valid(NEL(t))
        case Failure(e) => invalidNel(CSVParseErr(line, e.getMessage))
      }
    else
      invalidNel(CSVParseErr(line, "Unparsable line"))
  }

  def decode[T: CSVCodec](lines: NEL[String]): Validated[NEL[CSVParseErr], NEL[T]] =
    lines.map { line =>
      CSVCodec.decode[T](line)
    }.unwrap.reduce(_ |+| _)
}

case class CSVParseErr(line: String, errMsg: String)

object CSVParser {
  implicit val nelSemigroup: Semigroup[NEL[CSVParseErr]] = SemigroupK[NEL].algebra[CSVParseErr]
}
