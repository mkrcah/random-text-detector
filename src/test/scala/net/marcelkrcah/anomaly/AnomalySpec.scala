package net.marcelkrcah.anomaly

import org.scalatest._

class AnomalySpec extends FlatSpec with Matchers {

  "An app" should "should have working specs" in {
      Anomaly.toBigram("aa") should be (1)
  }
}
