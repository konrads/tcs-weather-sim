package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical._
import com.tcs.weathersim.util.{RepeatableSelector, Selector}
import org.scalatest.{FlatSpec, Matchers}

class ConditionServiceSpec extends FlatSpec with Matchers {
  implicit val selector: Selector = RepeatableSelector
  val service = new ConditionService

  "ConditionService" should "get Sunny" in {
    service.getCondition(Summer, Land, Temperature(30)) shouldBe Sunny
  }

  it should "get Rain" in {
    service.getCondition(Winter, Water, Temperature(10)) shouldBe Rain
  }

  it should "get Snow" in {
    service.getCondition(Winter, Water, Temperature(-10)) shouldBe Snow
  }
}
