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

import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraint
import play.api.libs.functional.syntax._
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._
import uk.gov.hmrc.agentservice.ValidationHelpers._
import uk.gov.hmrc.agentservice.models.BusinessDetails

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

object FormFieldInputType extends Enumeration {
  type formFieldInputType = Value
  val date, text = Value
}

case class IdentifyClient(clientId: String, knownFact: Option[String])
case class CustomerDataCheckRequest(knownFact: Option[String])
object CustomerDataCheckRequest {
  implicit val format: Format[CustomerDataCheckRequest] = Json.format[CustomerDataCheckRequest]
}
case class HodResponse[T](statusCode: Int,
                          customerName: Option[String],
                          knownFact: Option[T],
                          isUkCustomer: Option[Boolean],
                          clientIdLkUp: Option[String],
                          customerIsInsolvent: Option[Boolean])

object HodResponse {
  def apply[T](statusCode: Int): HodResponse[T] = HodResponse(statusCode, None, None, None, None, None)
}

case class HodRequestConfig[T](url: String, jsonReads: Reads[HodResponse[T]])

import play.api.libs.json.{Json, Reads, Writes, __}

sealed trait CustomerDataCheckResponse {
  val hodResponseCode: Int
}

object CustomerDataCheckResponse {
  implicit val reads: Reads[CustomerDataCheckResponse] =
    __.read[CustomerDataCheckSuccess].map(x => x: CustomerDataCheckResponse) orElse __.read[CustomerDataCheckUnsuccessful].map(x => x: CustomerDataCheckResponse)

  implicit val writes: Writes[CustomerDataCheckResponse] = Writes[CustomerDataCheckResponse]{
    case success: CustomerDataCheckSuccess => CustomerDataCheckSuccess.writes.writes(success)
    case failed: CustomerDataCheckUnsuccessful => CustomerDataCheckUnsuccessful.writes.writes(failed)
  }


}

case class CustomerDataCheckUnsuccessful(hodResponseCode: Int, message: String) extends CustomerDataCheckResponse

object CustomerDataCheckUnsuccessful {

  implicit val reads: Reads[CustomerDataCheckUnsuccessful] = Json.reads[CustomerDataCheckUnsuccessful]
  implicit val writes: Writes[CustomerDataCheckUnsuccessful] = Json.writes[CustomerDataCheckUnsuccessful]
}
case class CustomerDataCheckSuccess(
                                   hodResponseCode: Int,
                                     customerName: Option[String],
                                     isUkCustomer: Option[Boolean],
                                     clientIdLkUp: Option[String],
                                     knownFactSupplied: Boolean,
                                   knownFactCheck: Option[Boolean]
                                   ) extends CustomerDataCheckResponse

object CustomerDataCheckSuccess {
  implicit val reads: Reads[CustomerDataCheckSuccess] = Json.reads[CustomerDataCheckSuccess]
  implicit val writes: Writes[CustomerDataCheckSuccess] = Json.writes[CustomerDataCheckSuccess]
}

case class HodApiConfig(baseUrl: String, headers: Seq[(String, String)])

case class IdentifyClientTaxIdConfig(mapping: Mapping[String], hintRequired: Boolean, inputClass: Option[String], inputPattern: Option[String], inputMode: Option[String])
case class IdentifyClientKnownFactConfig(mapping: Mapping[Option[String]], formFieldInputType: FormFieldInputType.formFieldInputType, hintRequired: Boolean, autoComplete: Option[String])
case class IdentifyClientPageConfig(
                                     service: String,
                                     clientIdConfig: IdentifyClientTaxIdConfig,
                                     knownFactConfig: Option[IdentifyClientKnownFactConfig],
                                     formWithErrors: Option[Form[IdentifyClient]] = None){
  val form: Form[IdentifyClient] = formWithErrors.getOrElse(
    knownFactConfig.fold(
      Form(
        mapping(
          "clientId" -> clientIdConfig.mapping,
          "knownFact" -> optional(text)
        )(IdentifyClient.apply)(IdentifyClient.unapply)
      )
    )(knownFactConfig =>
      Form(
        mapping(
          "clientId" -> clientIdConfig.mapping,
          "knownFact" -> knownFactConfig.mapping
        )(IdentifyClient.apply)(IdentifyClient.unapply)
      )
    )
  )
}
abstract class AgentService[T] {

  val _id: String
  def identifyClientPageConfig(formWithErrors: Option[Form[IdentifyClient]]): IdentifyClientPageConfig

  def hodRequestConfig(clientId: String): HodRequestConfig[T]
  def backupHodRequestConfig(clientId: String): Option[HodRequestConfig[T]]

  val knownFactCheckRequired: Boolean

  val validateKnownFact: (Option[String], Option[T]) => Option[Boolean]

  val supportedClientEnrolmentServiceKeys: List[String]
}

class VatService extends AgentService[LocalDate] {

  override val _id: String = "vat"

  private val knownFactMapping: Mapping[Option[String]] = dateFieldsMapping(s"error.knownFact.${_id}")

  private val validVrn: Constraint[String] =
    validateVrnField(s"error.clientId.required.${_id}", s"error.clientId.regex-failure.${_id}")

  private val clientTaxIdMapping = normalizedText.verifying(validVrn)
  override def identifyClientPageConfig(formWithErrors: Option[Form[IdentifyClient]]): IdentifyClientPageConfig = IdentifyClientPageConfig(
    _id,
    IdentifyClientTaxIdConfig(
      mapping = clientTaxIdMapping,
      hintRequired = true,
      inputClass = Some("govuk-input--width-10"),
      inputPattern = Some("[0-9]*"),
      inputMode = Some("numeric")
    ),
    Option(
      IdentifyClientKnownFactConfig(
        mapping = knownFactMapping,
        FormFieldInputType.date,
        hintRequired = true,
        autoComplete = None
    )),
    formWithErrors
  )

  override val knownFactCheckRequired: Boolean = true

  override val validateKnownFact: (Option[String], Option[LocalDate]) => Option[Boolean] = (ms: Option[String], ot: Option[LocalDate]) =>
    for {
      s <- ms
      t <- ot
    } yield LocalDate.parse(s).isEqual(t)

  private val constructVatApiUri: String => String = (clientId: String) => s"/vat/customer/vrn/$clientId/information"

  override def hodRequestConfig(clientId: String): HodRequestConfig[LocalDate] = HodRequestConfig(
    url = constructVatApiUri(clientId),
    jsonReads = {
      (__ \ "approvedInformation").readNullable[JsObject].map {
        case Some(approvedInformation) =>
          val maybeDate: Option[LocalDate] = {
            (approvedInformation \ "customerDetails" \ "effectiveRegistrationDate").asOpt[String].map(LocalDate.parse)
          }
          val maybeName = (approvedInformation \ "customerDetails" \ "tradingName").asOpt[String]
          val isInsolvent = (approvedInformation \ "customerDetails" \ "isInsolvent").as[Boolean]
          HodResponse(200, maybeName, maybeDate, None, None, Some(isInsolvent))
        case _ => HodResponse.apply(200)
      }
    }
  )

  override def backupHodRequestConfig(clientId: String): Option[HodRequestConfig[LocalDate]] = Option.empty

  override val supportedClientEnrolmentServiceKeys: List[String] = List("HMRC-MTD-VAT")

}

class ItsaService extends AgentService[String] {
  override val _id: String = "itsa"
  private val clientTaxIdMapping = uppercaseNormalizedText.verifying(validNino)
  private val knownFactMapping = uppercaseNormalizedText.verifying(
    validPostcode("enter-postcode.invalid-format", "error.postcode.required", "enter-postcode.invalid-characters")
  ).transform[Option[String]](Option(_), _.get)
  override def identifyClientPageConfig(formWithErrors: Option[Form[IdentifyClient]]): IdentifyClientPageConfig = IdentifyClientPageConfig(
    _id,
    IdentifyClientTaxIdConfig(
      mapping = clientTaxIdMapping,
      hintRequired = true,
      inputClass = Some("govuk-input--width-10"),
      inputPattern = None,
      inputMode = None
    ),
    Option(
      IdentifyClientKnownFactConfig(
        mapping = knownFactMapping,
        FormFieldInputType.text,
        hintRequired = true,
        autoComplete = Some("postal-code")
      )),
    formWithErrors
  )

  override val knownFactCheckRequired: Boolean = true

  override val validateKnownFact: (Option[String], Option[String]) => Option[Boolean] = (ms: Option[String], ot: Option[String]) =>
    for {
      s <- ms
      t <- ot
    } yield s.equals(t)

  private val constructItsaApiUri: String => String = (clientId: String) => s"/registration/business-details/nino/$clientId"

  override def hodRequestConfig(clientId: String): HodRequestConfig[String] = HodRequestConfig(
    url = constructItsaApiUri(clientId),
    jsonReads = {
      (__ \ "taxPayerDisplayResponse").readNullable[BusinessDetails].map {
        case Some(businessDetails) =>
          val addressDetails = businessDetails.businessData.headOption.flatMap(_.businessAddressDetails)
          val isUkCustomer = addressDetails.map(_.countryCode == "GB")
          val knownFact = if(isUkCustomer.contains(true)) addressDetails.flatMap(_.postalCode) else None
          val clientIdLkUp = businessDetails.mtdId
          val tradingName = businessDetails.businessData.headOption.flatMap(_.tradingName)
          HodResponse(200, tradingName, knownFact, isUkCustomer, clientIdLkUp, None)
        case _ => HodResponse.apply(200)
      }
    }
  )

  private val constructCiDUri: String => String = (clientId: String) => s"/citizen-details/$clientId/designatory-details"
  override def backupHodRequestConfig(clientId: String): Option[HodRequestConfig[String]] =
    Option(
      HodRequestConfig(url = constructItsaApiUri(clientId),
        jsonReads = (
          (__ \ "person" \ "firstName").readNullable[String] and
            (__ \ "person" \ "lastName").readNullable[String] and
            (__ \ "address" \ "country").readNullable[String] and
                (__ \ "address" \ "postcode").readNullable[String])(
          (firstName,lastName,country,postcode) => {
            val name = for {
              first <- firstName
              last <- lastName
            } yield s"$first $last"
            val isUkCustomer = country.contains("GREAT BRITAIN")
            val knownFact = if(isUkCustomer) postcode else None
            HodResponse(200, name, knownFact, Option(isUkCustomer), None, None)
          }
        )
      )
    )
}

class CgtPdService extends AgentService[String] {
  override val _id: String = "cgt"

  private val clientIdMapping: Mapping[String] = text.verifying(_.matches("[A-Z]{5}"))
  override def identifyClientPageConfig(formWithErrors: Option[Form[IdentifyClient]]): IdentifyClientPageConfig =
    IdentifyClientPageConfig(_id,IdentifyClientTaxIdConfig(
      mapping = clientIdMapping, hintRequired = true, inputClass = None, inputPattern = None, inputMode = None
    ), knownFactConfig = None, formWithErrors = formWithErrors)

  override val knownFactCheckRequired: Boolean = true

  override val validateKnownFact: (Option[String], Option[String]) => Option[Boolean] = (ms: Option[String], ot: Option[String]) =>
    for {
      s <- ms
      t <- ot
    } yield s.equals(t)

  override def hodRequestConfig(clientId: String): HodRequestConfig[String] = ???

  override def backupHodRequestConfig(clientId: String): Option[HodRequestConfig[String]] = ???
}

@Singleton
class AgentServiceSupport @Inject() {

  private val vatService = new VatService
  private val itsaService = new ItsaService
  private val cgtService = new CgtPdService

  private val supportedServices = List(vatService, itsaService, cgtService)
  private def getService(id: String): AgentService[_] = supportedServices.find(_._id == id).get
  def identifyClientPageConfig(id: String, formWithErrors: Option[Form[IdentifyClient]] = None) = getService(id).identifyClientPageConfig(formWithErrors)

  def hodRequestConfig(id: String)(clientId: String): HodRequestConfig[_] = getService(id).hodRequestConfig(clientId)

  def knownFactCheck[T](id: String)(ms: Option[String], kf: Option[T]): Option[Boolean]  =
    (id, kf) match {
      case ("vat", x: Option[LocalDate])    => vatService.validateKnownFact(ms, x)
      case ("itsa", x: Option[String])      => itsaService.validateKnownFact(ms, x)
      case ("cgt",  x: Option[String])      => cgtService.validateKnownFact(ms, x)
    }
}
