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

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.agentmtdidentifiers.model.GroupId.{ENCODING, SPLITTER}

import java.net.{URLDecoder, URLEncoder}
import java.time.LocalDateTime

case class AccessGroup(
                        arn: Arn,
                        groupName: String,
                        created: LocalDateTime,
                        lastUpdated: LocalDateTime,
                        createdBy: AgentUser,
                        lastUpdatedBy: AgentUser,
                        teamMembers: Option[Set[AgentUser]],
                        clients: Option[Set[Enrolment]]
                      )

object AccessGroup {
  implicit val formatAccessGroup: OFormat[AccessGroup] = Json.format[AccessGroup]
}

case class AccessGroupSummary(groupId: String, groupName: String, clientCount: Int, teamMemberCount: Int)

object AccessGroupSummary {

  def convert(accessGroup: AccessGroup): AccessGroupSummary =
    AccessGroupSummary(
      GroupId(accessGroup.arn, accessGroup.groupName).encode,
      accessGroup.groupName,
      accessGroup.clients.fold(0)(_.size),
      accessGroup.teamMembers.fold(0)(_.size)
    )

  implicit val formatAccessGroupSummary: OFormat[AccessGroupSummary] = Json.format[AccessGroupSummary]
}

case class AccessGroupSummaries(groups: Seq[AccessGroupSummary], unassignedClients: Set[Client])

object AccessGroupSummaries {
  implicit val format: OFormat[AccessGroupSummaries] = Json.format[AccessGroupSummaries]
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
