package com.tcs.weathersim.util

/**
  * Codec for pipe char `|` separated values.
  */
trait PSVCodec[T] {
  def toElems(t: T): Seq[String]
}

object PSVCodec {
  def encode[T: PSVCodec](t: T): String =
    implicitly[PSVCodec[T]].toElems(t).mkString("|")
}
