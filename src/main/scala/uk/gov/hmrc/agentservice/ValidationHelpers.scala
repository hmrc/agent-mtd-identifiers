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

package uk.gov.hmrc.agentservice

import play.api.data.Forms.{of, text, tuple}
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import uk.gov.hmrc.agentmtdidentifiers.model.Vrn
import play.api.data.format.Formats._
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, ResolverStyle}

object ValidationHelpers {

  val dayFieldRegex = "^[0-9]{1,4}$"
  val monthFieldRegex = "^(0?[1-9]|1[012])$"
  val yearFieldRegex = "^[0-9]{1,4}$"
  val year = "year"
  val month = "month"
  val day = "day"

  def nonEmpty(failure: String): Constraint[String] = Constraint[String] { fieldValue: String =>
    if (fieldValue.trim.isEmpty) Invalid(ValidationError(failure)) else Valid
  }

  def validateField(emptyFailure: String, invalidFailure: String)(condition: String => Boolean) = Constraint[String] { fieldValue: String =>
    nonEmpty(emptyFailure)(fieldValue) match {
      case i: Invalid =>
        i
      case Valid =>
        if (condition(fieldValue.trim.toUpperCase))
          Valid
        else
          Invalid(ValidationError(invalidFailure))
    }
  }

  def validateVrnField(nonEmptyFailure: String, regexFailure: String) = Constraint[String] { fieldValue: String =>
    nonEmpty(nonEmptyFailure)(fieldValue) match {
      case i: Invalid =>
        i
      case Valid =>
        if (!Vrn.isValid(fieldValue))
          Invalid(ValidationError(regexFailure))
        else
          Valid
    }
  }

  val dateTimeFormat: DateTimeFormatter =
    DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT)

  def parseDate(date: String): Boolean =
    try {
      LocalDate.parse(date, dateTimeFormat)
      true
    } catch {
      case _: Throwable => false
    }

  val normalizedText: Mapping[String] = of[String].transform(_.replaceAll("\\s", ""), identity)

  private def invalid(messageKey: String, inputFieldClass: String): Invalid =
    Invalid(ValidationError(messageKey, "inputFieldClass" -> inputFieldClass))
  def validateDateFields(formMessageKey: String): Constraint[(String, String, String)] =
    Constraint[(String, String, String)] { s: (String, String, String) =>
      (s._1.matches(yearFieldRegex), s._2.matches(monthFieldRegex), s._3.matches(dayFieldRegex)) match {
        //y   //m   //d
        case a@(true, true, true) =>
          if (parseDate(s"${s._1}-${s._2}-${s._3}")) Valid
          else invalid(s"$formMessageKey-date.invalid-format", s"$day-$month-$year")
        case (true, true, false) => invalid(s"$formMessageKey-date.day", day)
        case (true, false, false) => invalid(s"$formMessageKey-date.day-month", s"$day-$month")
        case (true, false, true) => invalid(s"$formMessageKey-date.month", month)
        case (false, true, true) => invalid(s"$formMessageKey-date.year", year)
        case (false, false, true) =>
          invalid(s"$formMessageKey-date.month-year", s"$month-$year")
        case (false, true, false) => invalid(s"$formMessageKey-date.day-year", s"$day-$year")
        case (false, false, false) =>
          invalid(
            if (s._1.isEmpty && s._2.isEmpty && s._3.isEmpty) s"$formMessageKey-date.required"
            else s"$formMessageKey-date.invalid-format",
            s"$day-$month-$year"
          )
      }
    }
  def dateFieldsMapping(formMessageKey: String): Mapping[Option[String]] =
    tuple(
      "year" -> text,
      "month" -> text,
      "day" -> text
    ).verifying(validateDateFields(formMessageKey))
      .transform[Option[String]](
        {
          case (y, m, d) =>
            if (y.isEmpty || m.isEmpty || d.isEmpty) None
            else Option(LocalDate.of(y.toInt, m.toInt, d.toInt).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        },
        date =>
          try {
            val l = LocalDate.parse(date.get)
            (l.getYear.toString, l.getMonthValue.toString, l.getDayOfMonth.toString)
          } catch {
            case e: Exception => throw new IllegalArgumentException(s"unexpected date input pattern $e")
          }
      )


  val uppercaseNormalizedText: Mapping[String] = normalizedText.transform(_.toUpperCase, identity)

  val validNino: Constraint[String] =
    validateField("error.nino.required", "enter-nino.invalid-format")(nino => Nino.isValid(nino))

  val postcodeCharactersRegex = "^[a-zA-Z0-9 ]+$"
  val postcodeRegex = "^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$|BFPO\\s?[0-9]{1,5}$"
  def validPostcode(invalidFormatFailure: String, emptyFailure: String, invalidCharactersFailure: String): Constraint[String] =
    Constraint[String] { input: String =>
      if (input.isEmpty) Invalid(ValidationError(emptyFailure))
      else if (!input.matches(postcodeCharactersRegex)) Invalid(ValidationError(invalidCharactersFailure))
      else if (!input.matches(postcodeRegex)) Invalid(ValidationError(invalidFormatFailure))
      else Valid
    }


}
