package net.marcelkrcah.anomaly

import org.scalatest._

class AnomalySpec extends FunSpec with Matchers {

  describe("Anomaly detector") {
    it("should compute bigrams") {
      Anomaly.toNgram("aabaa", 2) should be (Map("aa"->2, "ab"->1, "ba"->1))
    }
    it("should compute 3-grams") {
      Anomaly.toNgram("aaabaaa", 3) should be (Map("aaa"->2, "aab"->1, "aba"->1, "baa"->1))
    }
  }

}
