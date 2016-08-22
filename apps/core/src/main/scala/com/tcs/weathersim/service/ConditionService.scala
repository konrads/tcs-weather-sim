package com.tcs.weathersim.service

import com.tcs.weathersim.model.canonical.{Land, MassType, Season, Summer, Water, Winter, _}
import com.tcs.weathersim.util.Selector

/**
  * Condition depends on:
  * - season, sunnier in summer
  * - more rain/snow over ocean
  * - temperature, the higher the sunnier
  */
class ConditionService(implicit selector: Selector) {
  def getCondition(season: Season, massType: MassType, temp: Temperature): Condition = {
    val seasonSunnyWeight = season match {
      case Summer => 0.8
      case Winter => 0.2
      case _ => 0.5
    }
    val massTypeSunnyWeight = massType match {
      case Land => 0.8
      case Water => 0.2
    }
    val isSunny = selector.select(left=false, right=true, seasonSunnyWeight, massTypeSunnyWeight)
    if (isSunny) Sunny
    else if (temp.value > 0) Rain else Snow
  }
}
