/*
 * Copyright 2020 HM Revenue & Customs
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

class UrnSpec extends FlatSpec with Matchers {

  it should "be true for a valid URN" in {
    Urn.isValid("XXTRUST80000001") shouldBe true
    Urn.isValid("XXTRUST80000010") shouldBe true
    Urn.isValid("XXTRUST80000100") shouldBe true
    Urn.isValid("XXTRUST80001000") shouldBe true
    Urn.isValid("11111111111111") shouldBe true
    Urn.isValid("AAAAAAAAAAAAAAA") shouldBe true
  }

  it should "be false when it has more than 15 digits" in {
    Urn.isValid("20000000000STUST") shouldBe false
  }

  it should "be false when it is empty" in {
    Urn.isValid("") shouldBe false
  }

  it should "be false when it has fewer than 15 digits" in {
    Urn.isValid("kk80000080000") shouldBe false
  }

  it should "be false when it has all numbers" in {
    Urn.isValid(urn = "9999999999999999") shouldBe false
  }

  it should "be false when its lowercase letters" in {
    Urn.isValid(urn = "sssssssssssssss") shouldBe false
  }
  it should "be false when it has non-digit characters" in {
    Urn.isValid("((TRUST80000001") shouldBe false
  }

  it should "be false when it has non-alphanumeric characters" in {
    Urn.isValid("200000000!") shouldBe false

  }
}
