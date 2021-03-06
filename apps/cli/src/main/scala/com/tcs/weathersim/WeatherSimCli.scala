package com.tcs.weathersim

import java.io.File

import cats.data.{NonEmptyList => NEL}
import cats.std.all._
import com.tcs.weathersim.model.{Simulation, SimulationReq}
import com.tcs.weathersim.util._
import com.typesafe.config.ConfigFactory

import scala.io.Source

/**
  * Command line entrypoint to the Weather simulator.
  * Expects to read the input from a csv file in SimulationReq CSV format, prints response in Simulation PSV format
  * (or prints input validation errors).
  */
object WeatherSimCli {

  private val validSelectors = Seq("repeatable", "random")
  case class Params(selectorName: String = "repeatable", configFile: Option[File] = None, inputFile: File = new File("input.csv"))

  def main(args: Array[String]) {
    val parser = new scopt.OptionParser[Params]("weather-sim") {
      opt[File]('c', "config").action { (c, params) =>
        params.copy(configFile = Some(c)) }.text("config file")
      opt[String]('s', "selector").action { (s, params) =>
        params.copy(selectorName = s) }
        .validate(s =>
          if (validSelectors.contains(s)) success
          else failure(s"selector must be: [${validSelectors.mkString(", ")}]")
        ).text("selector for measurements")
      arg[File]("input").action { (i, params) =>
        params.copy(inputFile = i) }.text("input file")
    }

    parser.parse(args, Params()) match {
      case None =>
        println(parser.usage)
        sys.exit(-1)
      case Some(Params(_, _, inputFile)) if ! inputFile.exists =>
        println(s"Cannot find file: ${inputFile.getName}!")
        sys.exit(-2)
      case Some(Params(selectorName, configFile, inputFile)) =>
        val config = configFile.map(ConfigFactory.parseFile).getOrElse(ConfigFactory.load())
        implicit val selector = if (selectorName == "random")
          new RandomSelector()
        else
          RepeatableSelector
        val weatherSim = new WeatherSim(config)
        val inputO = NEL.fromList(Source.fromFile(inputFile).getLines.toList)
        inputO.foreach { input =>
          val validated = CSVCodec.decode[SimulationReq](input)
          validated.fold(
            {
              errs =>
                val asStrs = errs.unwrap.map(err => s"line: ${err.line}, error: ${err.errMsg}")
                println(s"""Input errors:\n${asStrs.mkString("\n")}""")
                sys.exit(-3)
            },
            {
              simReqs =>
                val sims = simReqs.map(req => weatherSim.getSimulation(req))
                val asStrs = sims.map(PSVCodec.encode[Simulation])
                println(asStrs.unwrap.mkString("\n"))
            }
          )
        }
    }
  }
}
