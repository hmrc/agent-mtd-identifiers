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

import uk.gov.hmrc.domain.{SimpleObjectReads, SimpleObjectWrites, TaxIdentifier}

case class CbcNonUkId(value: String) extends TaxIdentifier


object CbcNonUkId {

  private val pattern = "^X[A-Z]CBC[0-9]{10}$".r

  def isValid(cbcNonUkId: String): Boolean =
    cbcNonUkId match {
      case pattern(_*) => true
      case _           => false
    }

  implicit val reads = new SimpleObjectReads[CbcNonUkId]("value", CbcNonUkId.apply)
  implicit val writes = new SimpleObjectWrites[CbcNonUkId](_.value)
}
