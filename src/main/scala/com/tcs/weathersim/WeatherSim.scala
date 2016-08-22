package com.tcs.weathersim

import com.tcs.weathersim.model.canonical._
import com.tcs.weathersim.model.{Simulation, SimulationReq}
import com.tcs.weathersim.service._
import com.tcs.weathersim.util._
import com.typesafe.config.Config

/**
  * Orchestrator for all services and data stores (PNG) required to produce simulation.
  */
class WeatherSim(config: Config)(implicit val selector: Selector) {
  private val pngElevation = config.getString("png_grid.elevation_resource")
  private val pngMassType = config.getString("png_grid.mass_type_resource")
  private val highestPointOnEarth = config.getDouble("constants.highest_point_on_earth")
  private val minHumidity = config.getInt("constants.min_humidity")
  private val maxHumidity = config.getInt("constants.max_humidity")
  private val minPressure = config.getDouble("constants.min_pressure")
  private val maxPressure = config.getDouble("constants.max_pressure")
  private val minTemp = config.getDouble("constants.min_temperature")
  private val maxTemp = config.getDouble("constants.max_temperature")

  private val elevationGrid = PNGGrid.readElevations(pngElevation, highestPointOnEarth)
  private val massTypeGrid = PNGGrid.readMassTypeMask(pngMassType)
  private val conditionService = new ConditionService
  private val humidityService = new HumidityService(minHumidity, maxHumidity, highestPointOnEarth)
  private val locationService = new LocationService(config)
  private val pressureService = new PressureService(minPressure, maxPressure, highestPointOnEarth)
  private val temperatureService = new TemperatureService(minTemp, maxTemp, highestPointOnEarth)

  def getSimulations(req: SimulationReq): Simulation = {
    val elev = elevationGrid(req.lat, req.long)
    val massType = massTypeGrid(req.lat, req.long)
    val season = Season.get(req.dt, req.lat)
    val temp = temperatureService.getTemperature(req.lat, elev, massType, req.dt.getHourOfDay, season)
    val condition = conditionService.getCondition(season, massType, temp)
    val pressure = pressureService.getPressure(elev)
    val location = locationService.getLocation(req.lat, req.long)
    val humidity = humidityService.getHumidity(elev, massType, season, req.dt.getHourOfDay)
    Simulation(location, req.lat, req.long, elev, req.dt, condition, temp, pressure, humidity)
  }
}
