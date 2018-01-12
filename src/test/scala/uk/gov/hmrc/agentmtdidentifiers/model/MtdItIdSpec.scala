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

package uk.gov.hmrc.agentmtdidentifiers.model

import org.scalatest.{FlatSpec, Matchers}

class MtdItIdSpec extends FlatSpec with Matchers {

  it should "be true for a valid MTDITID" in {
    MtdItId.isValid("A0000000000000a") shouldBe true
  }

  it should "be false when it has more than 15 digits" in {
    MtdItId.isValid("0000000000000000") shouldBe false
  }

  it should "be false when it is empty" in {
    MtdItId.isValid("") shouldBe false
  }

  it should "be false when it has non-alphanumeric characters" in {
    MtdItId.isValid("00000000000000!") shouldBe false
  }
}
