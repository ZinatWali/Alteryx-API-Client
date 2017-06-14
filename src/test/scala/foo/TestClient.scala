package foo

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.collection.JavaConverters._

class TestClient extends Simulation {
  val oAuth1 = new OAuth1()
  val url = "[Put in API URL that you want to test]"
  val consumerKey = "[Put in your API key]"
  val consumerSecret = "[Put in your API secret]"
  val signatureMethod = "HMAC-SHA1"
  val version = "1.0"

  val headers =
    Map(
      "Content-Length" -> "0",
      "Content-Type" -> "application/json",
      "Origin" -> "null",
      "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"
    )

  val feeder = Iterator.continually {
    val n = oAuth1.getNonce
    val ts = oAuth1.getTimestamp

    val oauthParams = Map(
      "oauth_consumer_key"-> consumerKey,
      "oauth_signature_method"-> signatureMethod,
      "oauth_nonce" -> n,
      "oauth_timestamp" -> ts,
      "oauth_version"-> "1.0")

    val signature = oAuth1.generateSignature(
      "POST",
      url,
      oauthParams.asJava,
      consumerSecret
    )

    Map (
      "nonce" -> n,
      "ts" -> ts,
      "signature" -> signature
    )
  }

  val scn: ScenarioBuilder = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .feed(feeder)
    .exec(
    http("request_1")
      .post(url)
      .headers(headers)
      .queryParam("oauth_consumer_key", consumerKey)
      .queryParam("oauth_signature_method", signatureMethod)
      .queryParam("oauth_nonce",  "${nonce}")
      .queryParam("oauth_timestamp", "${ts}")
      .queryParam("oauth_version", "1.0")
      .queryParam("oauth_signature", "${signature}")
  )

  setUp(scn.inject(atOnceUsers(100)).protocols(http))
}

