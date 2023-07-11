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

class EnrolmentKeySpec extends AnyFlatSpec with Matchers {

  "EnrolmentKey" should "generate enrolment keys correctly" in {
    EnrolmentKey.enrolmentKey("HMRC-MTD-IT", "someId") shouldBe "HMRC-MTD-IT~MTDITID~someId"
    EnrolmentKey.enrolmentKey("HMRC-MTD-VAT", "someId") shouldBe "HMRC-MTD-VAT~VRN~someId"
    EnrolmentKey.enrolmentKey("HMRC-TERS-ORG", "someId") shouldBe "HMRC-TERS-ORG~SAUTR~someId"
    EnrolmentKey.enrolmentKey("HMRC-TERSNT-ORG", "someId") shouldBe "HMRC-TERSNT-ORG~URN~someId"
    EnrolmentKey.enrolmentKey("HMRC-CGT-PD", "someId") shouldBe "HMRC-CGT-PD~CGTPDRef~someId"
    EnrolmentKey.enrolmentKey("HMRC-PPT-ORG", "someId") shouldBe "HMRC-PPT-ORG~EtmpRegistrationNumber~someId"
    // TODO intentionally not testing HMRC-CBC-ORG as we need to change it to also include a UTR.
    EnrolmentKey.enrolmentKey("HMRC-CBC-NONUK-ORG", "someId") shouldBe "HMRC-CBC-NONUK-ORG~cbcId~someId"
    EnrolmentKey.enrolmentKey("HMRC-PT", "someId") shouldBe "HMRC-PT~NINO~someId"
    an[Exception] shouldBe thrownBy(EnrolmentKey.enrolmentKey("badServiceId", "someId"))
  }
  it should "deconstruct enrolment keys correctly" in {
    EnrolmentKey.deconstruct("HMRC-MTD-IT~MTDITID~someId") shouldBe (("HMRC-MTD-IT", "someId"))
    EnrolmentKey.deconstruct("HMRC-MTD-VAT~VRN~someId") shouldBe (("HMRC-MTD-VAT", "someId"))
    EnrolmentKey.deconstruct("HMRC-TERS-ORG~SAUTR~someId") shouldBe (("HMRC-TERS-ORG", "someId"))
    EnrolmentKey.deconstruct("HMRC-TERSNT-ORG~URN~someId") shouldBe (("HMRC-TERSNT-ORG", "someId"))
    EnrolmentKey.deconstruct("HMRC-CGT-PD~CGTPDRef~someId") shouldBe (("HMRC-CGT-PD", "someId"))
    EnrolmentKey.deconstruct("HMRC-PPT-ORG~EtmpRegistrationNumber~someId") shouldBe (("HMRC-PPT-ORG", "someId"))
    // TODO intentionally not testing HMRC-CBC-ORG as we need to change it to also include a UTR.
    EnrolmentKey.deconstruct("HMRC-CBC-NONUK-ORG~cbcId~someId") shouldBe (("HMRC-CBC-NONUK-ORG", "someId"))
    EnrolmentKey.deconstruct("HMRC-PT~NINO~someId") shouldBe (("HMRC-PT", "someId"))
    an[Exception] shouldBe thrownBy(EnrolmentKey.deconstruct("HMRC-FAKE-SVC~NINO~AB123456Z"))
  }
  it should "build enrolment keys from enrolments correctly" in {
    val vatEnrolment = Enrolment("HMRC-MTD-VAT", "Activated", "Joe", Seq(Identifier("VRN", "123456789")))
    EnrolmentKey.fromEnrolment(vatEnrolment) shouldBe "HMRC-MTD-VAT~VRN~123456789"
    val cbcEnrolment = Enrolment("HMRC-CBC-ORG", "Activated", "Joe", Seq(Identifier("UTR", "0101010101"), Identifier("cbcId", "XACBC0123456789")))
    EnrolmentKey.fromEnrolment(cbcEnrolment) shouldBe "HMRC-CBC-ORG~UTR~0101010101~cbcId~XACBC0123456789"
  }
}


