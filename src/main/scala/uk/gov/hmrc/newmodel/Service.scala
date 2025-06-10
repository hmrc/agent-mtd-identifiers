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

import uk.gov.hmrc.agentmtdidentifiers.model._
import uk.gov.hmrc.domain.{Nino, TaxIdentifier}

import Implicits._


/**
 * A service 'as the user will see it', i.e. without any details of its technical representation.
 */
sealed trait VisibleService

object VisibleService {
  case object IncomeTax extends VisibleService
  case object PersonalIncomeRecord extends VisibleService
  case object Vat extends VisibleService
  case object Trust extends VisibleService
  case object CapitalGainsTax extends VisibleService
  case object PlasticPackagingTax extends VisibleService
  case object CountryByCountry extends VisibleService
  case object Pillar2 extends VisibleService
}

//--------------

/**
 * A service as it is represented internally - the technical details can now be made explicit
 */
sealed trait Service

case class EacdService[A](enrolmentType: EacdEnrolmentType[A]) extends Service // A well-behaved EACD-managed service.
case object LegacyVat extends Service // this service needs to be handled differently from all others and the type system reflects that
case object LegacyItsa extends Service // this service needs to be handled differently from all others and the type system reflects that
case object PersonalIncomeRecord extends Service // this service needs to be handled differently from all others and the type system reflects that

//---------------

abstract class EacdEnrolmentType[A](serviceKey: String)(implicit isa: IsEacdIdentifierSet[A]) {
//  def makeTypedEnrolmentKey

  def makeEnrolmentKey(identifiers: A): EnrolmentKey =
    EnrolmentKey(serviceKey, isa.toUntypedIdentifiers(identifiers))
}

/* Note: I have changed the HMRC-TERS-ORG from Utr to newly-defined tax identifier SaUtr,
   because their output eacd identifiers are different! ("SAUTR" vs. "UTR")
   The idea is that values that translates to different EACD representations should be different types. */

case object HmrcMtdIt extends EacdEnrolmentType[MtdItId]("HMRC-MTD-IT")
case object HmrcMtdVat extends EacdEnrolmentType[Vrn]("HMRC-MTD-VAT")
case object HmrcTersOrg extends EacdEnrolmentType[SaUtr]("HMRC-TERS-ORG")
case object HmrcTersntOrg extends EacdEnrolmentType[Urn]("HMRC-TERSNT-ORG")
case object HmrcCgtPd extends EacdEnrolmentType[CgtRef]("HMRC-CGT-PD")
case object HmrcPptOrg extends EacdEnrolmentType[PptRef]("HMRC-PPT-ORG")
case object HmrcCbcOrg extends EacdEnrolmentType[(CbcId, Utr)]("HMRC-CBC-ORG")
case object HmrcCbcNonukOrg extends EacdEnrolmentType[CbcId]("HMRC-CBC-NONUK-ORG")
case object HmrcPillar2Org extends EacdEnrolmentType[PlrId]("HMRC-PILLAR2-ORG")

// re-defining EnrolmmentKey here as it is not in scope, but do reuse the one already existing...
case class EnrolmentKey(serviceKey: String, identifiers: Seq[Identifier]) {
  override def toString: String = (serviceKey +: identifiers.flatMap(i => Seq(i.key, i.value))).mkString("~")
}

case class TypedEnrolmentKey[A: IsEacdIdentifierSet](service: EacdEnrolmentType[A], identifiers: A)

// -----------------------

/* Note: using a typeclass approach here because our tax identifiers are mixture
  of types we have defined ourselves and HMRC core domain types (that we do not control).
  So it's best not to rely on being able to define these methods on the type itself.
 */
trait IsEacdIdentifier[A] {
  val identifierKey: String
  def toUntypedIdentifier(a: A): Identifier
  // we could add an 'isValid' method here as well if desired
  def typed(value: String): A
}

object IsEacdIdentifier {
  val supportedIdentifiers: Seq[IsEacdIdentifier[_]] = Seq(ninoIdentifier, mtdItIdIdentifier, VrnIdentifier, SaUtrIdentifier, UtrIdentifier, UrnIdentifier, CgtRefIdentifier, PptRefRefIdentifier, CbcIdIdentifier, PlrIdIdentifier)
  def fromUntypedIdentifier(identifier: Identifier): Option[IsEacdIdentifier[_]] = supportedIdentifiers.find(_.identifierKey == identifier.key)
}

trait IsEacdIdentifierSet[AS] {
  def toUntypedIdentifiers(as: AS): Seq[Identifier]
}

// -----------------------

object Implicits {
  def eacdIdentifier[A <: TaxIdentifier](idKey: String, instantiate: String => A): IsEacdIdentifier[A] = new IsEacdIdentifier[A] {
    val identifierKey: String = idKey
    def toUntypedIdentifier(a: A): Identifier = Identifier(identifierKey, a.value)
    def typed(value: String): A = instantiate(value)
  }

  implicit val ninoIdentifier: IsEacdIdentifier[Nino] = eacdIdentifier("NINO", Nino.apply)
  implicit val mtdItIdIdentifier: IsEacdIdentifier[MtdItId] = eacdIdentifier("MTDITID", MtdItId.apply)
  implicit val VrnIdentifier: IsEacdIdentifier[Vrn] = eacdIdentifier("VRN", Vrn.apply)
  implicit val SaUtrIdentifier: IsEacdIdentifier[SaUtr] = eacdIdentifier("SAUTR", SaUtr.apply)
  implicit val UtrIdentifier: IsEacdIdentifier[Utr] = eacdIdentifier("UTR", Utr.apply)
  implicit val UrnIdentifier: IsEacdIdentifier[Urn] = eacdIdentifier("URN", Urn.apply)
  implicit val CgtRefIdentifier: IsEacdIdentifier[CgtRef] = eacdIdentifier("CGTPDRef", CgtRef.apply)
  implicit val PptRefRefIdentifier: IsEacdIdentifier[PptRef] = eacdIdentifier("EtmpRegistrationNumber", PptRef.apply)
  implicit val CbcIdIdentifier: IsEacdIdentifier[CbcId] = eacdIdentifier("cbcId", CbcId.apply)
  implicit val PlrIdIdentifier: IsEacdIdentifier[PlrId] = eacdIdentifier("PLRID", PlrId.apply)

  implicit def singleIdentifierSet[A](implicit ia: IsEacdIdentifier[A]): IsEacdIdentifierSet[A] = new IsEacdIdentifierSet[A] {
    def toUntypedIdentifiers(as: A): Seq[Identifier] = Seq(ia.toUntypedIdentifier(as))
  }
  implicit def tuple2IdentifierSet[A, B](implicit ia: IsEacdIdentifier[A], ib: IsEacdIdentifier[B]): IsEacdIdentifierSet[(A, B)] = new IsEacdIdentifierSet[(A, B)] {
    def toUntypedIdentifiers(as: (A, B)): Seq[Identifier] = Seq(ia.toUntypedIdentifier(as._1), ib.toUntypedIdentifier(as._2))
  }
}
