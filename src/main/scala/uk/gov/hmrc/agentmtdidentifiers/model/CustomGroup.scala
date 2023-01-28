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

import org.bson.types.ObjectId
import play.api.libs.json._
import uk.gov.hmrc.agentmtdidentifiers.model.GroupId.{ENCODING, SPLITTER}

import java.net.{URLDecoder, URLEncoder}
import java.time.LocalDateTime
import scala.util.Try

trait AccessGroup{
  def _id: ObjectId
  def arn: Arn
  def groupName: String
  def created: LocalDateTime
  def lastUpdated: LocalDateTime
  def createdBy: AgentUser
  def lastUpdatedBy: AgentUser
  def teamMembers: Option[Set[AgentUser]]
}

// Custom access group
case class CustomGroup(
                        _id: ObjectId,
                        arn: Arn,
                        groupName: String,
                        created: LocalDateTime,
                        lastUpdated: LocalDateTime,
                        createdBy: AgentUser,
                        lastUpdatedBy: AgentUser,
                        teamMembers: Option[Set[AgentUser]],
                        clients: Option[Set[Client]]
                      ) extends AccessGroup

object CustomGroup {

  def apply(arn: Arn,
            groupName: String,
            created: LocalDateTime,
            lastUpdated: LocalDateTime,
            createdBy: AgentUser,
            lastUpdatedBy: AgentUser,
            teamMembers: Option[Set[AgentUser]],
            clients: Option[Set[Client]]): CustomGroup = {

    CustomGroup(
      new ObjectId(), arn, groupName,
      created, lastUpdated, createdBy, lastUpdatedBy,
      teamMembers, clients)
  }

  implicit val objectIdFormat: Format[ObjectId] = Format(
    Reads[ObjectId] {
      case s: JsString =>
        val maybeOID: Try[ObjectId] = Try {
          new ObjectId(s.value)
        }
        if (maybeOID.isSuccess) JsSuccess(maybeOID.get) else {
          JsError("Expected ObjectId as JsString")
        }
      case _ => JsError()
    },
    Writes[ObjectId]((o: ObjectId) => JsString(o.toHexString))
  )

  implicit val formatAccessGroup: OFormat[CustomGroup] = Json.format[CustomGroup]
}

case class ClientList(assigned: Set[Client], unassigned: Set[Client])

object ClientList {
  implicit val format: OFormat[ClientList] = Json.format[ClientList]
}

case class GroupId(arn: Arn, groupName: String) {

  def encode: String =
    URLEncoder.encode(arn.value + SPLITTER + groupName, ENCODING)
}

object GroupId {

  private val ENCODING = "UTF-8"
  private val SPLITTER = "~"

  def decode(gid: String): Option[GroupId] =
    Option(gid).flatMap { gid =>
      if (gid.contains(" ")) {
        None
      } else {
        URLDecoder.decode(gid, ENCODING).split(SPLITTER) match {
          case parts if parts.length != 2 =>
            None
          case parts if Arn.isValid(parts(0)) =>
            Option(GroupId(Arn(parts(0)), parts(1).trim))
          case _ =>
            None
        }
      }
    }

}
