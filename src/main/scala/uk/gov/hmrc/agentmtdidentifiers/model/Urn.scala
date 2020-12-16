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

import uk.gov.hmrc.domain.{Modulus11Check, SimpleObjectReads, SimpleObjectWrites, TaxIdentifier}

case class Urn(value: String) extends TaxIdentifier {

}

object Urn {

  private val urnPattern = "^([A-Z0-9]{1,15})$".r

  def isValid(urn: String): Boolean =
    urn match {
      case urnPattern(_*) => UrnCheck.isValid(urn)
      case _              => false
    }

  implicit val urnReads = new SimpleObjectReads[Urn]("value", Urn.apply)
  implicit val urnWrites = new SimpleObjectWrites[Urn](_.value)
}

object UrnCheck extends Modulus11Check {

  def isValid(urn: String): Boolean = {
    val suffix: String = urn.substring(1)
    calculateCheckCharacter(suffix) == urn.charAt(0)
  }
}
