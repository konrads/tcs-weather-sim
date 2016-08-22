package com.tcs.weathersim.model.canonical

sealed trait Condition
case object Rain extends Condition
case object Snow extends Condition
case object Sunny extends Condition
