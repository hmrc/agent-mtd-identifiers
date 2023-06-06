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

import play.api.libs.json.Format
import uk.gov.hmrc.domain.{Nino, SimpleObjectReads, SimpleObjectWrites, TaxIdentifier}

sealed abstract class Service(
  val id: String,
  val invitationIdPrefix: Char,
  val enrolmentKey: String,
  val supportedSuppliedClientIdType: ClientIdType[_ <: TaxIdentifier],
  val supportedClientIdType: ClientIdType[_ <: TaxIdentifier],
  val requiresKnownFactsCheck: Boolean) {

  override def toString: String = this.id

  override def equals(that: Any): Boolean =
    that match {
      case that: Service => this.id.equals(that.id)
      case _             => false
    }
}

object IdentifierKeys{
  val mtdItId = "MTDITID"
  val hmrcNi = "HMRC-NI"
  val sautr = "SAUTR"
  val cgtPdRef = "CGTPDRef"
  val hmrcAsAgent = "HMRC-AS-AGENT"
  val etmpRegNum = "EtmpRegistrationNumber"
  val urn = "URN"
  val cbcId = "cbcId"
  val nino = "NINO"
  val vrn = "VRN"
}

object Service {

  val HMRC_MTD_IT = "HMRC-MTD-IT"
  val HMRC_PIR = "PERSONAL-INCOME-RECORD"
  val HMRC_MTD_VAT = "HMRC-MTD-VAT"
  val HMRC_TERS_ORG = "HMRC-TERS-ORG"
  val HMRC_TERSNT_ORG = "HMRC-TERSNT-ORG"
  val HMRC_CGT_PD = "HMRC-CGT-PD"
  val HMRC_PPT_ORG = "HMRC-PPT-ORG"
  val HMRC_PT = "HMRC-PT"
  val HMRC_CBC_ORG = "HMRC-CBC-ORG"
  val HMRC_CBC_NON_UK_ORG = "HMRC-CBC-NONUK-ORG"

  case object MtdIt extends Service(HMRC_MTD_IT, 'A', HMRC_MTD_IT, NinoType, MtdItIdType, true)
  case object PersonalIncomeRecord extends Service(HMRC_PIR, 'B', "HMRC-NI", NinoType, NinoType, false)
  case object Vat extends Service(HMRC_MTD_VAT, 'C', HMRC_MTD_VAT, VrnType, VrnType, false)
  case object Trust extends Service(HMRC_TERS_ORG, 'D', HMRC_TERS_ORG, UtrType, UtrType, false)
  case object TrustNT extends Service(HMRC_TERSNT_ORG, 'F', HMRC_TERSNT_ORG, UrnType, UrnType, false)
  case object CapitalGains extends Service(HMRC_CGT_PD, 'E', HMRC_CGT_PD, CgtRefType, CgtRefType, true)
  case object Ppt extends Service(HMRC_PPT_ORG, 'G', HMRC_PPT_ORG, PptRefType, PptRefType, true)
  case object Cbc extends Service(HMRC_CBC_ORG, 'H', HMRC_CBC_ORG, CbcIdType, CbcIdType, true)
  case object CbcNonUk extends Service(HMRC_CBC_NON_UK_ORG, 'I', HMRC_CBC_NON_UK_ORG, CbcIdType, CbcIdType, true)

  val supportedServices: Seq[Service] = Seq(MtdIt, Vat, PersonalIncomeRecord, Trust, TrustNT, CapitalGains, Ppt, Cbc, CbcNonUk)

  def findById(id: String): Option[Service] = supportedServices.find(_.id == id)
  def forId(id: String): Service = findById(id).getOrElse(throw new Exception("Not a valid service id: " + id))
  def forInvitationId(invitationId: InvitationId): Option[Service] =
    supportedServices.find(_.invitationIdPrefix == invitationId.value.head)

  def apply(id: String) = forId(id)
  def unapply(service: Service): Option[String] = Some(service.id)

  val reads = new SimpleObjectReads[Service]("id", Service.apply)
  val writes = new SimpleObjectWrites[Service](_.id)
  implicit val format = Format(reads, writes)

}

sealed abstract class ClientIdType[+T <: TaxIdentifier](
  val clazz: Class[_],
  val id: String,
  val enrolmentId: String,
  val createUnderlying: String => T) {
  def isValid(value: String): Boolean
}

object ClientIdType {
  val supportedTypes = Seq(NinoType, MtdItIdType, VrnType, UtrType, UrnType, CgtRefType, PptRefType)
  def forId(id: String) =
    supportedTypes.find(_.id == id).getOrElse(throw new IllegalArgumentException("Invalid id:" + id))
}

case object NinoType extends ClientIdType(classOf[Nino], "ni", "NINO", Nino.apply) {
  override def isValid(value: String): Boolean = Nino.isValid(value)
}

case object MtdItIdType extends ClientIdType(classOf[MtdItId], "MTDITID", "MTDITID", MtdItId.apply) {
  override def isValid(value: String): Boolean = MtdItId.isValid(value)
}

case object VrnType extends ClientIdType(classOf[Vrn], "vrn", "VRN", Vrn.apply) {
  override def isValid(value: String) = Vrn.isValid(value)
}

case object UtrType extends ClientIdType(classOf[Utr], "utr", "SAUTR", Utr.apply) {
  override def isValid(value: String) = value.matches("^\\d{10}$")
}

case object UrnType extends ClientIdType(classOf[Urn], "urn", "URN", Urn.apply) {
  override def isValid(value: String) = value.matches("^([A-Z0-9]{1,15})$")
}

case object CgtRefType extends ClientIdType(classOf[CgtRef], "CGTPDRef", "CGTPDRef", CgtRef.apply) {
  override def isValid(value: String): Boolean = CgtRef.isValid(value)
}

case object PptRefType extends ClientIdType(classOf[PptRef], "EtmpRegistrationNumber", "EtmpRegistrationNumber", PptRef.apply) {
  override def isValid(value: String): Boolean = PptRef.isValid(value)
}

case object CbcIdType extends ClientIdType(classOf[CbcId], "cbcId", "cbcId", CbcId.apply) {
  override def isValid(value: String): Boolean = CbcId.isValid(value)
}

case class ClientIdentifier[T <: TaxIdentifier](underlying: T) {

  private val clientIdType = ClientIdType.supportedTypes
    .find(_.clazz == underlying.getClass)
    .getOrElse(throw new Exception("Invalid type for clientId " + underlying.getClass.getCanonicalName))

  val value: String = underlying.value
  val typeId: String = clientIdType.id
  val enrolmentId: String = clientIdType.enrolmentId

  override def toString: String = value
}

object ClientIdentifier {
  type ClientId = ClientIdentifier[_ <: TaxIdentifier]

  def apply(value: String, typeId: String): ClientId =
    ClientIdType.supportedTypes
      .find(_.id == typeId)
      .getOrElse(throw new IllegalArgumentException("Invalid Client Id Type: " + typeId))
      .createUnderlying(value.replaceAll("\\s", ""))

  implicit def wrap[T <: TaxIdentifier](taxId: T): ClientIdentifier[T] = ClientIdentifier(taxId)
}
