package com.tcs.weathersim.model.canonical

/**
  * Identifies Land/Water surface.
  */
sealed trait MassType
case object Water extends MassType
case object Land extends MassType
