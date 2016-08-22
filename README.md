Weather Simulation
==================

Architecture
------------

Main entrypoint is the `WeatherSimApp`. It accepts command line parameters, reads the config and input files, instantiates `WeatherSim`.
`WeatherSim` is an orchestrator service, responsible for:

* lookup of configuration constants
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


To run
------

Note: running of the following requires pre-loading of PNGs, which takes a while due to their large sizes (18Mb and 4Mb). 

To run tests:

    sbt test

To run repeatable simulation:

    sbt "run valid-input.csv"
    
To run randomized simulation:

    sbt "run -s random valid-input.csv"
    
To generate some input errors:

    sbt "run invalid-input.csv"
