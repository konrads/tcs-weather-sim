package com.tcs.weathersim.model.canonical

sealed trait MassType
case object Water extends MassType
case object Land extends MassType
