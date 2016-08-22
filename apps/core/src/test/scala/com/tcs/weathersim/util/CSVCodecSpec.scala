package com.tcs.weathersim.util

import com.tcs.weathersim.model.SimulationReq
import com.tcs.weathersim.model.canonical._
import cats.data.Validated.{invalid, valid}
import cats.data.{NonEmptyList => NEL}
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}

class CSVCodecSpec extends FlatSpec with Matchers {
  "CSVCodec" should "decode simulationReq" in {
    val input = NEL("11,22,1970-01-01T00:00:00+00:00", "33,44,1970-01-01T00:00:00+00:00")
    val expected = valid(NEL(
      SimulationReq(Latitude(11), Longitude(22), new DateTime(0)),
      SimulationReq(Latitude(33), Longitude(44), new DateTime(0))))
    CSVCodec.decode[SimulationReq](input) shouldBe expected
  }

  it should "fail to decode invalid format" in {
    val input = NEL("aa,bb,cc", "dd,ee,ff")
    val expected = invalid(NEL(
      CSVParseErr("aa,bb,cc", "For input string: \"aa\""),
      CSVParseErr("dd,ee,ff", "For input string: \"dd\"")))
    CSVCodec.decode[SimulationReq](input) shouldBe expected
  }

  it should "fail to decode unparsable line" in {
    val input = NEL("aa,bb,cc,dd,ee,ff")
    val expected = invalid(NEL(
      CSVParseErr("aa,bb,cc,dd,ee,ff", "Unparsable line")))
    CSVCodec.decode[SimulationReq](input) shouldBe expected
  }
}
