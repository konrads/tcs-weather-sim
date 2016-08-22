package com.tcs.weathersim.util

/**
  * Codec for pipe char `|` separated values.
  */
trait PSVCodec[T] {
  def toElems(t: T): Seq[String]
  // in the future:
  // def read(s: String): T
}

object PSVCodec {
  def encode[T: PSVCodec](t: T): String =
    implicitly[PSVCodec[T]].toElems(t).mkString("|")
}
