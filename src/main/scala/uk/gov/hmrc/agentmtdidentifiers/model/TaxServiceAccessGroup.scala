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

package uk.gov.hmrc.agentmtdidentifiers.model

import org.bson.types.ObjectId
import play.api.libs.json._
import java.time.LocalDateTime
import scala.util.Try

case class TaxServiceAccessGroup(
                        _id: ObjectId,
                        arn: Arn,
                        groupName: String,
                        created: LocalDateTime,
                        lastUpdated: LocalDateTime,
                        createdBy: AgentUser,
                        lastUpdatedBy: AgentUser,
                        teamMembers: Option[Set[AgentUser]],
                        service: String, // Nice to use Service but how to handle trusts?
                        automaticUpdates: Boolean,
                        excludedClients: Option[Set[Client]]
                      )

object TaxServiceAccessGroup {

  def apply(arn: Arn,
            groupName: String,
            created: LocalDateTime,
            lastUpdated: LocalDateTime,
            createdBy: AgentUser,
            lastUpdatedBy: AgentUser,
            teamMembers: Option[Set[AgentUser]],
            service: String,
            automaticUpdates: Boolean = true, // if false, new clients added to excluded clients
            excludedClients: Option[Set[Client]]): TaxServiceAccessGroup = {

    TaxServiceAccessGroup(
      new ObjectId(), arn, groupName,
      created, lastUpdated, createdBy, lastUpdatedBy,
      teamMembers, service, automaticUpdates, excludedClients)
  }

  implicit val objectIdFormat: Format[ObjectId] = Format(
    Reads[ObjectId] {
      case s: JsString =>
        val maybeOID: Try[ObjectId] = Try{new ObjectId(s.value)}
        if(maybeOID.isSuccess) JsSuccess(maybeOID.get) else {
          JsError("Expected ObjectId as JsString")
        }
      case _ => JsError()
    },
    Writes[ObjectId]((o: ObjectId) => JsString(o.toHexString))
  )

  implicit val formatAccessGroup: OFormat[TaxServiceAccessGroup] = Json.format[TaxServiceAccessGroup]
}

