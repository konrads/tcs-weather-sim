Weather Simulation
==================

Architecture
------------

There are 2 entrypoints: `WeatherSimCli` and `WeatherSimTcpServer`. Both take command line params and read the config file, and boostrap by reading large PNG files (18Mb and 4Mb). `WeatherSimCli` processes the input, generates and prints the `Simulations`, and exits. However due to the boostrap, this causes a performance hit on every invocation. `WeatherSimTcpServer` only bootstraps once and awaits requests via TCP socket.

Both entrypoint instantiate `WeatherSim`, which orchestrates construction of `Simulation`s by:

* loading PNG data for `Elevation` and `Land`/`Water` (ie. `MassType`) mask
* creating a `Selector` which provides the ability to chose a value based on some weights. Implementations of the `Selector`:
  * `RepeatableSelector` - always returns the same selection for the same weights
  * `RandomSelector` - adds an element of randomization 
* creating sub-services responsible for generating `Simulation`'s measurements. The measurement values are produced by generating weights as per business rules, the plausible values, and feeding them both to the `Selector`:
  * ConditionService - determines `Condition` (ie. `Sunny`/`Rain`/`Snow`): on `Season`, `Land`/`Water` (ie. `MassType`), `Temperature` 
  * HumidityService - determines `Humidity` %, based on: `Elevation`, `MassType`, `Season`, `hourOfDay`
  * LocationService - determines if the `Latitude`/`Longitude` has a predefined label. The labels are set up in the config file
  * PressureService - determines air `Pressure`, based on `Elevation`
  * TemperatureService - determines air `Temperature`, based on: `Latitude`, `Elevation`, `MassType`, `hourOfDay`, `Season`

Sub-services make use of following utilities:

* `PNGGrid` - loads PNG, forms a searchable grid, and provides lookups for `Elevation` and `MassType`
* `CSVCodec` - decodes CSV into case classes, utilizes cats' `Validated` applicative
* `PSVCodec` - encodes PSV (pipe-seperated-values) for output
* `Selector` - for value selection based on weights generated from business rules

Entities used throughout are classified into:

* canonical - value classes wrapping primitive types, and enum-like entities. Comprise measurements of `Simulation`, as well as helper values such as `Season` and `MassType` used within sub-services
* composite entities comprising of canonicals - used for input/output: `Simulation`, `SimulationReq`


Project structure
-----------------

Multi-project approach was used to cater for multiple entrypoints:

* apps/core - the engine of the simulator, with tests
* apps/cli - contains `WeatherSimCli` executable
* apps/tcp_server - contains `WeatherSimTcpServer` executable


Assumptions
-----------

* locations are Earth based, with `Latitude`/`Longitude` coordinates
* following constants (supplied in `application.conf`) represent extreme values used in weight calculations:
  * highest_point_on_earth = 8848
  * min_humidity = 20
  * max_humidity = 80
  * min_pressure = 300
  * max_pressure = 1200
  * min_temperature = -20.0
  * max_temperature = 40.0
* `Season` boundaries (defined as `SeasonalBoundaries`) begin at the start of: March, June, September, December
* config file *must* follow the format of the default `application.conf`
* Doubles are used for most measurements, except for `Humidity` which uses Int %. Should rounding-off cause discrepancies, alternative would be to switch to Int/Long, and increase the precision by multiplying by 100 or 1000 


To test
-------

Note: some tests also load the large PNGs, hence run bit slower. 
```bash
sbt test
```

To assemble deployable artifacts
--------------------------------
```bash
sbt assembly
```

To run
------

Via cli:

* repeatable simulation:
```bash
java -jar apps/cli/target/scala-2.11/weather-sim-cli-assembly-1.0.jar valid-input.csv
# or
sbt "cli/run valid-input.csv"
```
    
* randomized simulation:
```bash
java -jar apps/cli/target/scala-2.11/weather-sim-cli-assembly-1.0.jar -s random valid-input.csv
# or
sbt "cli/run -s random valid-input.csv"
```

* with input errors:
```bash
java -jar apps/cli/target/scala-2.11/weather-sim-cli-assembly-1.0.jar invalid-input.csv
# or
sbt "cli/run invalid-input.csv"
```

Via tcp-server:

* start the tcp-server:
```bash
java -jar apps/tcp_server/target/scala-2.11/weather-sim-tcp-server-assembly-1.0.jar
# or
sbt "tcp_server/run"
```    

* run the client
```bash    
cat valid-input.csv | netcat 127.0.0.1 6666
# ctrl-c
cat invalid-input.csv | netcat 127.0.0.1 6666
```
