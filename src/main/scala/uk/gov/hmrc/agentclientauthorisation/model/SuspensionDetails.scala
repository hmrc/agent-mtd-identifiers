/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.agentclientauthorisation.model

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.agentclientauthorisation.model.Service._

case class SuspensionDetails(suspensionStatus: Boolean, suspendedRegimes: Set[String]) {

  def isRegimeSuspended(service: Service): Boolean = {
    val regime = SuspensionDetails.serviceToRegime(service)
    suspendedRegimes.contains(regime)
  }

  def isRegimeSuspended(id: String): Boolean = {
    def idToService(id: String): Service = {
      SuspensionDetails.serviceToRegime
        .find(_._1.id == id)
        .map(_._1)
        .getOrElse(throw new IllegalArgumentException(s"Service of ID '$id' not known"))
    }

    val regime = SuspensionDetails.serviceToRegime(idToService(id))
    suspendedRegimes.contains(regime)
  }

  def suspendedRegimesForServices(serviceIds: Set[String]): Set[String] = {
    SuspensionDetails.serviceToRegime
      .filterKeys(s => serviceIds.contains(s.id)).values.toSet
      .intersect(suspendedRegimes)
  }

  def isAnyRegimeSuspendedForServices(ids: Set[String]): Boolean = suspendedRegimesForServices(ids).nonEmpty

  override def toString: String = suspendedRegimes.toSeq.sorted.mkString(",")

}

object SuspensionDetails {

  lazy val serviceToRegime: Map[Service, String] =
    Map(MtdIt -> "ITSA", Vat -> "VATC", Trust -> "TRS", TrustNT -> "TRS", CapitalGains -> "CGT", Ppt -> "PPT")

  //PERSONAL-INCOME-RECORD service has no enrolment / regime so cannot be suspended
  lazy val validSuspensionRegimes: Set[String] = serviceToRegime.filterKeys(Seq(MtdIt, Vat, Trust, CapitalGains, Ppt).contains(_)).values.toSet

  def apply(suspensionStatus: Boolean, regimes: Option[Set[String]]): SuspensionDetails = {
    val suspendedRegimes =
      regimes.fold(Set.empty[String])(rs => if (rs.contains("ALL") || rs.contains("AGSV")) validSuspensionRegimes else rs)

    new SuspensionDetails(suspensionStatus, suspendedRegimes)
  }

  implicit val formats: OFormat[SuspensionDetails] = Json.format

  val notSuspended = SuspensionDetails(suspensionStatus = false, None)
}

case class SuspensionDetailsNotFound(message: String) extends Exception(message)
