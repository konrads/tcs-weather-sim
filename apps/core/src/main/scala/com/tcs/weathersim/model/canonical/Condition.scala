package com.tcs.weathersim.model.canonical

/**
  * General weather condition, with predefined values for Rain, Snow, Sunny.
  */
sealed trait Condition
case object Rain extends Condition
case object Snow extends Condition
case object Sunny extends Condition
