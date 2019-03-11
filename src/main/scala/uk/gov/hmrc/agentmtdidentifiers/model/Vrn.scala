/*
 * Copyright 2019 HM Revenue & Customs
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

case class Vrn(value: String) extends TaxIdentifier

object Vrn {
  implicit val vrnReads = new SimpleObjectReads[Vrn]("value", Vrn.apply)
  implicit val vrnWrites = new SimpleObjectWrites[Vrn](_.value)

  private[model] def calcCheckSum97(total: Int): Int = {
    val mod = total % 97
    if (mod == 0) 97 else 97 - mod
  }

  private[model] def calcCheckSum9755(total: Int): Int = calcCheckSum97(total + 55)

  private[model] def weightedTotal(reference: String): Int = {
    val weighting = List(8, 7, 6, 5, 4, 3, 2)
    val ref = reference.map(_.asDigit).take(7)
    (ref, weighting).zipped.map(_ * _).sum
  }

  private[model] def takeCheckSumPart(vrn: String): Int = vrn.takeRight(2).toInt

  private[model] def regexCheck(vrn: String): Boolean = vrn.matches("[0-9]{9}")

  def isValid(vrn: String): Boolean =
    if (regexCheck(vrn)) {
      val total = weightedTotal(vrn)
      val checkSumPart = takeCheckSumPart(vrn)
      if (checkSumPart == calcCheckSum97(total)) true
      else checkSumPart == calcCheckSum9755(total)
    } else false
}
