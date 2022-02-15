/*
 * Copyright 2022 HM Revenue & Customs
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

import uk.gov.hmrc.domain.{SimpleObjectReads, SimpleObjectWrites, TaxIdentifier}

case class Eori(value: String) extends TaxIdentifier

object Eori {

  private val eoriPattern = "^[0-9A-Za-z]{1,17}$".r

  def isValid(eori: String): Boolean =
    eori match {
      case eoriPattern(_*) => true
      case _               => false
    }

  implicit val eoriReads = new SimpleObjectReads[Eori]("value", Eori.apply)
  implicit val eoriWrites = new SimpleObjectWrites[Eori](_.value)
}
