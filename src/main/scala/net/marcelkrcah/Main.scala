package net.marcelkrcah


import net.marcelkrcah.detector.RandomTextDetector
import net.marcelkrcah.detector.MathUtils._

import scala.io.Source

object Main extends App with ResultPrinter with HttpServer{

  val argsSizeOk = args.length == 2
  lazy val argsActionOk = List("start-server", "batch").contains(args(0))
  if ( !argsSizeOk || !argsActionOk ) {
    println("""Usage: random-text-detector start-server|batch filename
      |Options:
      |  batch         Compute suspicious score for each word in a given list and print the result to stdin.
      |  start-server  Start an http server on the port specified by the PORT env property or 8080 if not specified.
      |                Call GET /api/detect?q=your-word to get a suspicious score for your-word.
      |                Navigate your browser to / or /index.html for the interactive version.
      |  filename      File containing a list of words, each word on a separate line.
      |""".stripMargin('|')
    )
    sys.exit(1)
  }

  val startServer = args(0) == "start-server"
  val filename = args(1)
  val verbose = false
  val randomWordsTotal = 20
  val defaultPort = 8080

  val words = Source.fromFile(filename).getLines().map(_.toLowerCase.trim).toList
  val detector = new RandomTextDetector(words)

  if (words.isEmpty) {
    println("Error: Cannot load words")
    sys.exit(1)
  }

  if (startServer) {
    val httpPort = scala.util.Properties.envOrElse("PORT", defaultPort.toString).toInt
    startServer(httpPort)
  } else {
    printVerboseResults()
    printSummaryForRandomWords()
  }

}


trait HttpServer {

  def detector: RandomTextDetector

  def startServer(httpPort: Int) {
    import akka.actor.{ActorSystem, Props}
    import akka.io.IO
    import spray.can.Http
    import akka.pattern.ask
    import akka.util.Timeout
    import scala.concurrent.duration._

    implicit val system = ActorSystem("on-spray-can")
    val service = system.actorOf(
      Props(classOf[DetectorHttpServiceActor], detector), "detector-service")
    implicit val timeout = Timeout(5.seconds)

    IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = httpPort)
  }

}


trait ResultPrinter {

  def detector: RandomTextDetector
  def words: List[String]
  def randomWordsTotal: Int

  case class Result(word:String, score: Double, isRandom: Boolean, ngramWeights: StringVector[Double])

  lazy val results: List[Result] = {
    val randomWords = words.take(randomWordsTotal)
    words.map { word =>
      val (score, ngrams) = detector.getScore(word)
      Result(word, score, randomWords.contains(word), ngrams)
      Result(word, score, randomWords.contains(word), ngrams)
    }.sortBy(_.score).reverse
  }


  def printVerboseResults() = {

    def vectorToString(v: StringVector[Double]) =
      v.toList.sortBy(_._2).reverse.map(x => f"${x._1}(${x._2}%2.2f)").mkString(", ")

    val isRandomColName = if (randomWordsTotal > 0) "IsRandom\t" else ""
    println(s"Order\tScore\tWord\t${isRandomColName}Ngrams")

    val maxWordLength = words.map(_.length).max

    val resultsAsStr = results.zipWithIndex.map {
      case (Result(word, score, isRandom, ngrams), index) =>
        val order = index + 1
        val isRandomMark = if (randomWordsTotal == 0) "" else if (isRandom) "*\t" else " \t"
        val wordPadded = word.padTo(maxWordLength, " ").mkString
        f"$order%2d\t$score%.2f\t$wordPadded\t$isRandomMark${vectorToString(ngrams)}"
    }
    println(resultsAsStr.mkString("\n"))
  }


  def printSummaryForRandomWords() = {
    def randomWordsAmongTop(n:Int) = results.take(n).count(_.isRandom)
    if (randomWordsTotal > 0) {

      def summaryForTop(n: Int) =
        s" - among top $n words with highest random score: ${randomWordsAmongTop(n)}"

      println("=======================================")
      println(s"Number of random words:")
      println(s" - in the whole dataset: $randomWordsTotal")
      println(summaryForTop(randomWordsTotal/2))
      println(summaryForTop(randomWordsTotal))
      println(summaryForTop(randomWordsTotal*2))
      println("=======================================")
    }
  }


}
