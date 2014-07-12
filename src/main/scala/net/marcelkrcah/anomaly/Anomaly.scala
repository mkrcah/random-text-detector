package net.marcelkrcah.anomaly

import scala.io.Source

object Anomaly extends App{

  val names = {
    val rawNames = Source.fromFile("data/names.txt").getLines()
    def normalize(rawName:String) = rawName.toLowerCase.trim
    rawNames.map(normalize)
  }

  println("aabbaa".sliding(2).toList.groupBy(identity).mapValues(_.length))

   def toNgram(s:String, n:Int) = {
     s.sliding(n).toList.groupBy(identity).mapValues(_.length)
   }

//   println(names.mkString("\n"))

 }
