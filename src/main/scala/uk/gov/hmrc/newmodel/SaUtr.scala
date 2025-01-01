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

package uk.gov.hmrc.newmodel

import uk.gov.hmrc.domain.{Modulus11Check, SimpleObjectReads, SimpleObjectWrites, TaxIdentifier}

case class SaUtr(value: String) extends TaxIdentifier

object SaUtr extends Modulus11Check {

  private val utrPattern = "^\\d{10}$".r

  def isValid(saUtr: String): Boolean =
    saUtr match {
      case utrPattern(_*) => calculateCheckCharacter(saUtr.substring(1)) == saUtr.charAt(0)
      case _              => false
    }

  implicit val utrReads = new SimpleObjectReads[SaUtr]("value", SaUtr.apply)
  implicit val utrWrites = new SimpleObjectWrites[SaUtr](_.value)
}
