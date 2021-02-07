import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class ComputerDBSimulation extends Simulation {

  val protocol: HttpProtocolBuilder = http
    .baseUrl("http://computer-database.gatling.io")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val simulation: ScenarioBuilder = scenario("Computer DB Simulation") // Scenario builder amd name for the simulation
    .exec(http("Open database") // Step to execute, HTTP builder and request name
      .get("/")) // Target URL which point to the base url
    .pause(5 seconds) // Wait 5 seconds
    .exec(http("Find lenovo") // Next HTTP call to execute
      .get("/computers") // http://computer-database.gatling.io/computers target url
      .queryParam("f", "lenovo") // add query parameter &f=lenovo
      .check((substring("lenovo thinkpad r400").exists))) // verify that response body contains lenovo thinkpad r400 string
    .exec(http("Open computer page")
      .get("/computers/484") // http://computer-database.gatling.io/computers target url
      .check((substring("lenovo thinkpad r400").exists)))
    .exec(http("Update computer information")
      .post("/computers/484") // POST method call and http://computer-database.gatling.io/computers/404 target url
      .header("Content-Type", "application/x-www-form-urlencoded") // add Content-Type header
      .body(StringBody("name=lenovo+thinkpad+r400&introduced=2020-01-01&discontinued=2020-02-02&company=4"))) // add string body

  setUp(
    simulation.inject(atOnceUsers(1)) // setup load profile, 1 user
  ).protocols(protocol) // add protocol object
}
