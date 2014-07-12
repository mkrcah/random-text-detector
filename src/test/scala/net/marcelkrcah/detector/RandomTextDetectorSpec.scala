package net.marcelkrcah.detector

import org.scalatest._
import net.marcelkrcah.detector.MathUtils._

class RandomTextDetectorSpec extends FunSpec with Matchers with NLPUtils {

  val epsilon=0.00001

  describe("Detector") {

    val detector = new RandomTextDetector(List("aabaa", "abb", "ababa"))

    it("should compute ngram counts") {
      detector.termCount shouldBe Map("aa"->2, "ab"->4, "ba"->3, "bb"->1)
    }

    it("should compute idf") {
      detector.idf("aa") shouldBe math.log(5.0/3) +- epsilon
    }

    it("should compute scores") {
      detector.getScore("AbB")._1 shouldBe
        detector.idf("ab") + detector.idf("bb")  +- epsilon
    }
  }

  describe("StringMatrix") {

    val matrix = List(
      Map("aa"->2, "ab"->1, "ba"->1),
      Map("ab"->1, "bb"->1),
      Map("ab"->2, "ba"->2))

    it("should sum by column") {
      matrix.groupByColumn(_.sum) shouldBe Map("aa"->2, "ab"->4, "ba"->3, "bb"->1)
    }

    it("should compute counts by column") {
      matrix.groupByColumn(_.size) shouldBe Map("aa"->1, "ab"->3, "ba"->2, "bb"->1)
    }
  }

  describe("StringVector") {

    it("should add weights") {
      Map("x"->2d, "y"->3d).weightBy(Map("x"->4d, "y"->5d)) shouldBe Map("x"->8d, "y"->15d)
    }
  }


  describe("NLPUtils") {

    it("should compute bigrams ") {
      toNgramVector(2)("aabaa") shouldBe Map("aa" -> 2, "ab" -> 1, "ba" -> 1)
    }

    it("should compute 3-grams ") {
      toNgramVector(3)("aaabaaa") shouldBe Map("aaa" -> 2, "aab" -> 1, "aba" -> 1, "baa" -> 1)
    }

    it("should compute IDF") {
      toStandardIdf(10)(0) shouldBe (2.303 +- 0.001 )
      toStandardIdf(10)(9) shouldBe (0d +- 0.001 )
    }

  }
}
