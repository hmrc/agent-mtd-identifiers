/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.agentmtdidentifiers.model

import org.scalatest.{FlatSpec, Matchers}

class VrnSpec extends FlatSpec with Matchers {

  val reference97 = "101747696"
  val reference9755 = "101747641"

  "isValid" should "be true for a valid VRN that passes Mod-97" in {
    val total: Int = Vrn.weightedTotal(reference97)
    val checkSum: Int = Vrn.takeCheckSumPart(reference97)

    Vrn.calcCheckSum97(total) shouldBe checkSum
    Vrn.isValid(reference97) shouldBe true
  }

  "isValid" should "be true for a valid VRN that fails Mod-97 but passes Mod-9755" in {
    val total: Int = Vrn.weightedTotal(reference9755)
    val checkSum: Int = Vrn.takeCheckSumPart(reference9755)

    Vrn.calcCheckSum97(total) should not be checkSum
    Vrn.calcCheckSum9755(total) shouldBe checkSum
    Vrn.isValid(reference9755) shouldBe true
  }

  "isValid" should "be false if the VRN's check digits fail both Mod-97 and Mod-9755" in {
    val testRefFail: String = s"${reference97.take(8)}1"
    val total: Int = Vrn.weightedTotal(testRefFail)
    val checkSum: Int = Vrn.takeCheckSumPart(testRefFail)

    Vrn.calcCheckSum97(total) should not be checkSum
    Vrn.calcCheckSum9755(total) should not be checkSum
    Vrn.isValid(testRefFail) shouldBe false
  }

  "calcCheckSum97" should "return 97 if the total equals to some multiple of 97" in {
    Vrn.calcCheckSum97(97) shouldBe 97
    Vrn.calcCheckSum97(194) shouldBe 97
  }

  "calcCheckSum9755" should "return 97 if the total equals to some multiple of 97 - 55" in {
    Vrn.calcCheckSum9755(97 - 55) shouldBe 97
    Vrn.calcCheckSum9755(194 - 55) shouldBe 97
  }

  "weightTotal" should "return sum of the weighted digits" in {
    Vrn.weightedTotal(reference97) shouldBe 98
    Vrn.weightedTotal(reference9755) shouldBe 98
    Vrn.weightedTotal("111111100") shouldBe 8 + 7 + 6 + 5 + 4 + 3 + 2
    Vrn.weightedTotal("777777700") shouldBe 56 + 49 + 42 + 35 + 28 + 21 + 14
  }

  "regexCheck" should "return true if the vrn matches regex" in {
    Vrn.regexCheck(reference97) shouldBe true
    Vrn.regexCheck(reference9755) shouldBe true
  }

  "regexCheck" should "return false if the vrn does not match regex" in {
    Vrn.regexCheck(reference97 + "1") shouldBe false
    Vrn.regexCheck(reference9755 + "1") shouldBe false
    Vrn.regexCheck("7777777") shouldBe false
    Vrn.regexCheck("7777777AA") shouldBe false
    Vrn.regexCheck("AAAAAAAAA") shouldBe false
  }
}
