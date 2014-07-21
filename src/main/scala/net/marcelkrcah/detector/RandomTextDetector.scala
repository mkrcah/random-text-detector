package net.marcelkrcah.detector
import net.marcelkrcah.detector.MathUtils._

class RandomTextDetector(words: List[String]) extends NLPUtils {

  val toNgrams = toNgramVector(2) _

  val tfVectors = words.map(w => (w, toNgrams(w))).toMap
  val termCount = tfVectors.values.groupByColumn(_.sum)

  val idf = {
    val idfFn = toAdjustedIdf(termCount.values.max) _
//    val idfFn = toStandardIdf(words.size) _
    termCount.mapValues(idfFn).withDefaultValue(idfFn(0))
  }

  def getScore(word: String): (Double, StringVector[Double]) = {
    val tfidf = toNgrams(word.toLowerCase).mapValues(_.toDouble).weightBy(idf)
    (tfidf.values.sum, tfidf)
  }

}


trait NLPUtils {

  def toNgramVector(n:Int)(s: String): StringVector[Int] =
    s.sliding(n).toList.groupBy(identity).mapValues(_.length)

  def toStandardIdf(documentsTotal:Int)(termCount:Int) =
    math.log(documentsTotal.toDouble / (termCount + 1))

  def toAdjustedIdf(maxTermCount:Int)(termCount:Int) = 
    math.log((maxTermCount + 1).toDouble / (termCount + 1))

}


object MathUtils {

  type StringVector[T] = Map[String, T]
  type StringMatrix[T] = Iterable[StringVector[T]]

  implicit class VectorOps[T](v: Map[T, Double]) {
    def weightBy(weight: Map[T, Double]) =
      v.map { case (key, a) => (key, a * weight(key))}
  }

  implicit class MatrixOps[T](xs: StringMatrix[T])(implicit num: Numeric[T]) {
    def groupByColumn[A](fn: Iterable[T] => A): StringVector[A] =
      xs.flatMap(identity).groupBy(_._1).mapValues(column => fn(column.map(_._2)))
  }


}
