package com.tcs.weathersim.util

import cats.data.Validated.{Invalid, Valid, invalidNel, valid}
import cats.data.{NonEmptyList => NEL, Validated, Xor}
import cats.std.all._
import cats.{Semigroup, _}

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

  def decode[T: CSVCodec](lines: Iterator[String]): Xor[NEL[CSVParseErr], Seq[T]] = {
    if (lines.isEmpty)
      Xor.Right(Seq.empty[T])
    else {
      val validated = lines.map(CSVCodec.decode[T]).reduce(_ combine _)
      validated match {
        case Invalid(err) => Xor.Left(err)
        case Valid(simReqs) => Xor.Right(simReqs.unwrap)
      }
    }
  }
}

case class CSVParseErr(line: String, errMsg: String)

object CSVParser {
  implicit val nelSemigroup: Semigroup[NEL[CSVParseErr]] = SemigroupK[NEL].algebra[CSVParseErr]
}
