package com.tcs.weathersim.model.canonical

import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}

class CanonicalSpec extends FlatSpec with Matchers {
  val monthLatSeason = Seq(
    // northern hemisphere
    (1,  50, Winter),
    (2,  50, Winter),
    (3,  50, Spring),
    (4,  50, Spring),
    (5,  50, Spring),
    (6,  50, Summer),
    (7,  50, Summer),
    (8,  50, Summer),
    (9,  50, Autumn),
    (10, 50, Autumn),
    (11, 50, Autumn),
    (12, 50, Winter),
    // southern hemisphere
    (1,  -5, Summer),
    (2,  -5, Summer),
    (3,  -5, Autumn),
    (4,  -5, Autumn),
    (5,  -5, Autumn),
    (6,  -5, Winter),
    (7,  -5, Winter),
    (8,  -5, Winter),
    (9,  -5, Spring),
    (10, -5, Spring),
    (11, -5, Spring),
    (12, -5, Summer)
  )

  "Season" should "be obtained for correct dates" in {
    val midMonth = new DateTime(2016, 1, 15, 0, 0)
    for {
      (month, lat, season) <- monthLatSeason
    } yield {
      Season.get(midMonth.withMonthOfYear(month), Latitude(lat)) shouldBe season
    }
  }
}
