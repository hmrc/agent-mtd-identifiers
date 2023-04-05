/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.agentmtdidentifiers.utils

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.agentmtdidentifiers.model.{Arn, Utr}

import uk.gov.hmrc.agentmtdidentifiers.utils.TaxIdentifierFormatters.{ArnOps, UtrOps}

class TaxIdentifierFormattersSpec extends AnyFlatSpec with Matchers {

  "Arn.prettifyStrict" should "return Some(hyphenated arn) if arn IS VALID" in {
    Arn("TARN0000001").prettifyStrict shouldBe Some("TARN-000-0001")
  }

  "Arn.prettifyStrict" should "return None if arn is NOT VALID" in {
    Arn("TARN00aaa00001").prettifyStrict shouldBe None
  }

  "Arn.prettify" should "return hyphenated arn if arn IS VALID" in {
    Arn("TARN0000001").prettify shouldBe "TARN-000-0001"
  }

  "Arn.prettify" should "return original value if ARN is NOT VALID" in {
    Arn("TARN00aaa00001").prettify shouldBe "TARN00aaa00001"
  }

  "Utr.prettifyStrict" should "return Some(utr with space in middle) when VALID UTR" in {
    Utr("2000000000").prettifyStrict shouldBe Some("20000 00000")
  }

  "Utr.prettifyStrict" should "return None whenever INVALID length" in {
    Utr("200").prettifyStrict shouldBe None
  }

  "Utr.prettifyStrict" should "return None whenever INVALID" in {
    Utr("200000000B").prettifyStrict shouldBe None
  }

  "Utr.prettify" should "return utr with space in middle as Utr length is always 10" in {
    Utr("2000000000").prettify shouldBe "20000 00000"
  }

  "Utr.prettify" should "return original value if INVALID" in {
    Utr("20000000001").prettify shouldBe "20000000001"
  }
}
