package net.marcelkrcah

import akka.actor.Actor
import net.marcelkrcah.detector.MathUtils.StringVector
import net.marcelkrcah.detector.RandomTextDetector
import spray.routing._
import spray.http._
import MediaTypes._
import spray.json._
import DefaultJsonProtocol._

class DetectorHttpServiceActor(val detector:RandomTextDetector) extends Actor with DetectorHttpService {
  def actorRefFactory = context
  def receive = runRoute(route)
}


trait DetectorHttpService extends HttpService {

  def detector: RandomTextDetector

  val MaxWordLength = 30

  val route =
    path("") {
      getFromResource("webapp/index.html")
    } ~
    pathPrefix("") {
      getFromResourceDirectory("webapp")
    } ~
    path("api" / "detect") {
      get {
        parameters('q) { word =>
          respondWithMediaType(`application/json`) {
            complete {
              val wordNormalized = word.trim.toLowerCase.take(MaxWordLength).mkString("")
              val (score, ngrams) = detector.getScore(wordNormalized)
              val jsonResult = ApiCommands.getScore(wordNormalized, score, ngrams)
              jsonResult.prettyPrint
            }
          }
        }
      }
    }
}


object ApiCommands {

  def getScore(word: String, score: Double, ngrams: StringVector[Double]):JsValue = {
    JsObject(
      "word" -> JsString(word),
      "score" -> JsNumber(score),
      "ngrams" -> JsArray(ngrams.toList.sortBy(_._2).reverse.map{
        case (ngram, ngramScore) => JsObject(
          "ngram" -> JsString(ngram),
          "score" -> JsNumber(ngramScore)
        ).toJson }.toList)
    )
  }
}