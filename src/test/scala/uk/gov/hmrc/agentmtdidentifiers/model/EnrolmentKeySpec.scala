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

package uk.gov.hmrc.agentmtdidentifiers.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.agentmtdidentifiers.model.EnrolmentKey.{deconstruct, enrolmentKey}
import uk.gov.hmrc.agentmtdidentifiers.model.IdentifierKeys._
import uk.gov.hmrc.agentmtdidentifiers.model.Service._

class EnrolmentKeySpec extends AnyFlatSpec with Matchers {

  "EnrolmentKey" should "generate enrolment keys correctly" in {
    enrolmentKey(HMRC_MTD_IT, "someId") shouldBe s"$HMRC_MTD_IT~$mtdItId~someId"
    enrolmentKey(HMRC_MTD_VAT, "someId") shouldBe s"$HMRC_MTD_VAT~$vrn~someId"
    enrolmentKey(HMRC_TERS_ORG, "someId") shouldBe s"$HMRC_TERS_ORG~$sautr~someId"
    enrolmentKey(HMRC_TERSNT_ORG, "someId") shouldBe s"$HMRC_TERSNT_ORG~$urn~someId"
    enrolmentKey(HMRC_CGT_PD, "someId") shouldBe s"$HMRC_CGT_PD~$cgtPdRef~someId"
    enrolmentKey(HMRC_PPT_ORG, "someId") shouldBe s"$HMRC_PPT_ORG~$etmpRegNum~someId"
    enrolmentKey(HMRC_PT, "someId") shouldBe s"$HMRC_PT~$nino~someId"
    an[Exception] shouldBe thrownBy(enrolmentKey("badServiceId", "someId"))
  }
  it should "deconstruct enrolment keys correctly" in {
    deconstruct(s"$HMRC_MTD_IT~$mtdItId~someId") shouldBe ((HMRC_MTD_IT, "someId"))
    deconstruct(s"$HMRC_MTD_VAT~$vrn~someId") shouldBe ((HMRC_MTD_VAT, "someId"))
    deconstruct(s"$HMRC_TERS_ORG~$sautr~someId") shouldBe ((HMRC_TERS_ORG, "someId"))
    deconstruct(s"$HMRC_TERSNT_ORG~$urn~someId") shouldBe ((HMRC_TERSNT_ORG, "someId"))
    deconstruct(s"$HMRC_CGT_PD~$cgtPdRef~someId") shouldBe ((HMRC_CGT_PD, "someId"))
    deconstruct(s"$HMRC_PPT_ORG~$etmpRegNum~someId") shouldBe ((HMRC_PPT_ORG, "someId"))
    deconstruct(s"$HMRC_PT~$nino~someId") shouldBe ((HMRC_PT, "someId"))
    an[Exception] shouldBe thrownBy(deconstruct("HMRC-FAKE-SVC~NINO~AB123456Z"))
  }
}


