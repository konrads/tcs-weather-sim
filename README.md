Weather Simulation
==================

Architecture
------------

Main entrypoint is the `WeatherSimApp`. It accepts command line parameters, reads the config and input file, instantiates `WeatherSim`.
`WeatherSim` is an orchestrator service, responsible for:

* lookup of configuration constants
* loading PNG data for elevation and ocean/land mask
* creating a `Selector` for choosing a value based on some weights. Implementations of the `Selector` are:
  * `RepeatableSelector` - always returns the same selection for identical weights
  * `RandomSelector` - adds an element of randomization 
* creating sub-services responsible for generating `Simulation`'s measurements. The measurement values are produced by generating weights as per business rules, the range of plausible values, and feeding them both to the `Selector`:
  * ConditionService - determines Condition (ie. Sunny/Rain/Snow): on Season, Land/Water (ie. MassType), Temperature 
  * HumidityService - determines humidity %, based on: Elevation, MassType, Season, hourOfDay
  * LocationService - determines if the latitude/longitude has a predefined label. The labels are set in the config file
  * PressureService - determines air pressure, based on Elevation
  * TemperatureService - determines air temperature, based on: Latitude, Elevation, MassType, hourOfDay, Season

Sub-services make use of following utilities:

* PNGGrid - capable of loading a PNG, and providing Elevation and Land/Mass information from them
* CSVCodec - decodes CSV into required entities, via cats' `Validated` applicative
* PSVCodec - encodes PSV (pipe-seperated-values) for output
* Selector - for value selection based on buisness rule weights

Entities used throughout are classified by:

* canonical value classes and enum-like entities. Comprise Simulation measurements, as well as helper values such as Season and MassType
* more complex entities, used for input/output: `Simulation`, `SimulationReq`


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
* Season boundaries (defined as `SeasonalBoundaries`) begin at the start of: March, June, September, December
* config file needs to follow the format of the default `application.conf`
* Doubles are used for most measurements, except for Humidity. Should rounding-off cause discrepancies, alternative path would be to switch to Int/Long, and increase the precision by multiplying by 100 or 1000 


To run
------

To run tests (note, the tests load PNGs, hence take longer than usual unit tests):

    sbt test

To run repeatable simulation:

    sbt "run valid-input.csv"
    
To run randomized simulation:

    sbt "run -s random valid-input.csv"
    
To observe errors:

    sbt "run invalid-input.csv"
