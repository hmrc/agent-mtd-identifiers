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

object EnrolmentKey {
  def enrolmentKey(serviceId: String, clientId: String): String = {
    val mService = Service.findById(serviceId)
    mService match {
      // case Some(Service.Cbc) => // TODO in future the enrolment key for HMRC-CBC-ORG will need to also take an UTR
      case Some(service)                  => s"${service.id}~${service.supportedClientIdType.enrolmentId}~" + clientId
      case None if serviceId == "HMRC-PT" => "HMRC-PT~NINO~" + clientId
      case _                              => throw new IllegalArgumentException(s"Service not supported: $serviceId")
    }
  }

  def enrolmentKeys(enrolment: Enrolment): Seq[String] = enrolment.identifiers.map(identifier => enrolmentKey(enrolment.service, identifier.value))

  /**
   * Returns serviceId and clientId from a given enrolmentKey
   */
  def deconstruct(ek: String): (String, String) = {
    val serviceId = ek.takeWhile(_ != '~')
    val clientId = ek.split('~').last
    // sanity check: try to reconstruct the original enrolment key
    if (enrolmentKey(serviceId, clientId).toUpperCase != ek.toUpperCase)
      throw new IllegalArgumentException(s"Unexpected enrolment key format: $ek")
    (serviceId, clientId)
  }
}
