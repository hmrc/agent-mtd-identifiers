/*
 * Copyright 2018 HM Revenue & Customs
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

import org.scalatest.{FlatSpec, Matchers}
import uk.gov.hmrc.agentmtdidentifiers.model.Arn

import uk.gov.hmrc.agentmtdidentifiers.utils.TaxIdentifierFormatters.ArnOps

class TaxIdentifierFormattersSpec extends FlatSpec with Matchers {

  "Arn.prettify" should "return hyphenated arn if arn IS VALID" in {
    Arn("TARN0000001").prettify shouldBe "TARN-000-0001"
  }

  "Arn.prettify" should "return original value if ARN is NOT VALID" in {
    Arn("TARN00aaa00001").prettify shouldBe "TARN00aaa00001"
  }

  "Arn.prettifyStrict" should "return Some(hyphenated arn) if arn IS VALID" in {
    Arn("TARN0000001").prettifyStrict shouldBe Some("TARN-000-0001")
  }

  "Arn.prettifyStrict" should "return None if arn is NOT VALID" in {
    Arn("TARN00aaa00001").prettifyStrict shouldBe None
  }
}
