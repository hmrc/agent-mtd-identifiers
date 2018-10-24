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

import uk.gov.hmrc.agentmtdidentifiers.model.{Arn, Utr}

object TaxIdentifierFormatters {

  implicit class ArnOps(arn: Arn) {

    def prettifyStrict: Option[String] = {
      if (Arn.isValid(arn.value)) {
        val unapplyPattern = """([A-Z]ARN)(\d{3})(\d{4})""".r
        unapplyPattern
          .unapplySeq(arn.value)
          .map(_.mkString("-"))
      }
      else None
    }

    def prettify: String = {
      prettifyStrict.getOrElse(arn.value)
    }
  }

  implicit class UtrOps(utr: Utr) {

    def prettifyStrict: Option[String] = {
      Utr.isValid(utr.value) match {
        case true if utr.value.trim.length == 10 => {
          val (first, last) = utr.value.trim.splitAt(5)
          Some(s"$first $last")
        }
        case _ => None
      }
    }

    def prettify: String = {
      prettifyStrict.getOrElse(utr.value)
    }
  }

}