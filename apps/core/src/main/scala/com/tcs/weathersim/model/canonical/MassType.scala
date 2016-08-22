package com.tcs.weathersim.model.canonical

/**
  * Helper type for identifying land/water masses
  */
sealed trait MassType
case object Water extends MassType
case object Land extends MassType
