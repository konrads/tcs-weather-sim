package com.tcs.weathersim

import java.io.File

import akka.actor._
import akka.stream.ActorAttributes._
import akka.stream.Supervision._
import akka.stream._
import akka.stream.scaladsl.{Flow, Framing, _}
import akka.util.ByteString
import cats.std.all._
import com.tcs.weathersim.model.{Simulation, SimulationReq}
import com.tcs.weathersim.util.{RandomSelector, _}
import com.typesafe.config.ConfigFactory

import scala.language.higherKinds
import scala.util._


/**
  * TCp server utilizing akka streaming, based on:
  * https://gist.github.com/sschaef/bd5ee6273ddaa7b015af
  * http://doc.akka.io/docs/akka/2.4.9-RC1/scala/stream/stream-error.html
  *
  * manual test:
  * cat valid-input.csv | netcat 127.0.0.1 6666
  */
object WeatherSimTcpServer {

  private val validSelectors = Seq("repeatable", "random")

  private case class Params(
                             selectorName: String = "repeatable",
                             configFile: Option[File] = None,
                             serverAddress: String = "0.0.0.0",
                             serverPort: Int = 6666)

  implicit val system = ActorSystem("tcp-server")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  def main(args: Array[String]) {
    val parser = new scopt.OptionParser[Params]("tcp-server") {
      opt[File]('c', "config").action { (c, params) =>
        params.copy(configFile = Some(c)) }.text("config file")
      opt[String]('s', "selector").action { (s, params) =>
        params.copy(selectorName = s) }
        .validate(s =>
          if (validSelectors.contains(s)) success
          else failure(s"selector must be: [${validSelectors.mkString(", ")}]")
        ).text("selector for measurements")
      opt[String]("address").action { (a, params) =>
        params.copy(serverAddress = a)
      }.text("server address")
      opt[Int]("port").action { (p, params) =>
        params.copy(serverPort = p)
      }.text("server port")
    }

    parser.parse(args, Params()) match {
      case Some(Params(selectorName, configFile, serverAddress, serverPort)) =>
        val config = configFile.map(ConfigFactory.parseFile).getOrElse(ConfigFactory.load())
        implicit val selector = if (selectorName == "random")
          new RandomSelector()
        else
          RepeatableSelector
        val weatherSim = new WeatherSim(config)

        val connectionHandler = Sink.foreach[Tcp.IncomingConnection] { conn =>
          conn.handleWith(inFlow(weatherSim))
        }
        val incomingConns = Tcp().bind(serverAddress, serverPort)
        val binding = incomingConns.to(connectionHandler).run()

        binding onComplete {
          case Success(b) =>
            println(s"Server started, listening on: ${b.localAddress}")
          case Failure(e) =>
            println(s"Server could not be bound to $serverAddress:$serverPort: ${e.getMessage}")
            sys.exit(-1)
        }
      case None =>
        parser.showUsage()
        sys.exit(-1)
    }
  }

  def inFlow(weatherSim: WeatherSim) = Flow[ByteString]
    .withAttributes(supervisionStrategy(resumingDecider))  // ensure Future failures don't crash the flow
    .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
    .map(_.utf8String)
    .map {
      line =>
        val validated = CSVCodec.decode[SimulationReq](line)
        validated.fold(
          {
            errs =>
              val asStrs = errs.unwrap.map(err => s"line: ${err.line}, error: ${err.errMsg}")
              ByteString(s"Failed... ${asStrs.mkString("\n")}\n")
          },
          {
            simReqs =>
              val sims = simReqs.unwrap.map(req => weatherSim.getSimulation(req))
              val asStrs = sims.map(PSVCodec.encode[Simulation])
              ByteString(asStrs.mkString("\n")+"\n")
          }
        )
    }

}