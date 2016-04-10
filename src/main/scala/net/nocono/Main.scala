package net.nocono

import java.io.{ InputStream, OutputStream }
import java.net.URLDecoder

import com.fasterxml.jackson.databind.{ DeserializationFeature, ObjectMapper }
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class Main {
  val scalaMapper = new ObjectMapper().registerModule(new DefaultScalaModule).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def handler(input: InputStream, output: OutputStream): Unit = {
    val params = scalaMapper.readValue(input, classOf[SlackOutgoingData])
    println(params)
    val result = params.decodedText match {
      case "こんにちは" => Some(s"こんにちは, ${params.user_name}")
      case "Scala"      => Some("こわくない")
      case _            => None
    }

    result.foreach { r =>
      output.write( s"""{ "text": "$r" }""".getBytes("UTF-8"))
    }
  }
}

case class SlackOutgoingData(
  token: String,
  team_id: String,
  team_domain: String,
  service_id: String,
  channel_id: String,
  channel_name: String,
  timestamp: String,
  user_id: String,
  user_name: String,
  text: String,
  trigger_word: String) {

  val decodedText = URLDecoder.decode(text, "UTF-8")

}