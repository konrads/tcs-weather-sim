package com.tcs.weathersim.util

import org.scalatest.{FlatSpec, Matchers}

class SelectorSpec extends FlatSpec with Matchers {
  "RepeatableSelector" should "choose a Double" in {
    RepeatableSelector.select(11.0, 22.0, 1, 0.9, 0.8) shouldBe 20.9
    RepeatableSelector.select(11.0, 22.0, 0.5, 0.2) shouldBe 14.85
  }

  it should "choose an Int" in {
    RepeatableSelector.select(11, 22, 1, 0.9, 0.8) shouldBe 20
    RepeatableSelector.select(11, 22, 0.5, 0.2) shouldBe 14
  }

  it should "choose a String" in {
    RepeatableSelector.select("ignored", "chosen", 1, 0.9, 0.8) shouldBe "chosen"
    RepeatableSelector.select("chosen", "ignored", 0.5, 0.2) shouldBe "chosen"
  }

  it should "repeat" in {
    RepeatableSelector.select(11, 22, 1, 0.9, 0.8) shouldBe 20
    RepeatableSelector.select(11, 22, 1, 0.9, 0.8) shouldBe 20
    RepeatableSelector.select(11, 22, 1, 0.9, 0.8) shouldBe 20
  }

  "RandomSelector" should "be close to RepeatableSelector" in {
    val s = new RandomSelector()
    s.select(11.0, 22.0, 0.5, 0.2) should be (14.5 +- 3)
    s.select(11.0, 22.0, 0.5, 0.2) should be (14.5 +- 3)
    s.select(11.0, 22.0, 0.5, 0.2) should be (14.5 +- 3)
  }

  it should "not repeat" in {
    val s = new RandomSelector()
    s.select(11.0, 22.0, 0.5, 0.2) should not be s.select(11.0, 22.0, 0.5, 0.2)
  }
}
