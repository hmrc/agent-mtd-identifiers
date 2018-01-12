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

object VrnValidation {

  private[model]def calcCheckSum97(total: Int): Int = {
    val mod = total % 97
    if (mod == 0) 97 else 97 - mod
  }

  private[model]def calcCheckSum9755(total: Int): Int = calcCheckSum97(total + 55)

  private[model]def weightedTotal(reference: String): Int = {
    val weighting = List(8, 7, 6, 5, 4, 3, 2)
    val ref = reference.map(_.asDigit).take(7)
    (ref, weighting).zipped.map(_ * _).sum
  }

  private[model] def takeCheckSumPart(vrn: String): Int = vrn.takeRight(2).toInt

  def isValid(vrn: String): Boolean = {
    val total = weightedTotal(vrn)
    if (takeCheckSumPart(vrn) == calcCheckSum97(total)) true
    else if (takeCheckSumPart(vrn) == calcCheckSum9755(total)) true
    else false
  }
}