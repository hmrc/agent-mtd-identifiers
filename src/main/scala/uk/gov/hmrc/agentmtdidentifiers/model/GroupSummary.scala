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

import play.api.libs.json._

case class GroupSummary(
                         groupId: String,
                         groupName: String,
                         clientCount: Option[Int],
                         teamMemberCount: Int,
                         taxService: Option[String] = None // if empty, custom group
                       )

object GroupSummary {

  def fromAccessGroup(accessGroup: CustomGroup): GroupSummary =
    GroupSummary(
      accessGroup._id.toHexString,
      accessGroup.groupName,
      Some(accessGroup.clients.fold(0)(_.size)),
      accessGroup.teamMembers.fold(0)(_.size),
      None
    )

  def fromAccessGroup(taxGroup: TaxGroup): GroupSummary =
    GroupSummary(
      taxGroup._id.toHexString,
      taxGroup.groupName,
      None, // info not retained in group - group could be empty
      taxGroup.teamMembers.fold(0)(_.size),
      Some(taxGroup.service)
    )

  def fromAccessGroup(accessGroup: TaxGroup, clientCount: Int): GroupSummary =
    GroupSummary(
      accessGroup._id.toHexString,
      accessGroup.groupName,
      Some(clientCount), // info not retained in group - group could be empty
      accessGroup.teamMembers.fold(0)(_.size),
      Some(accessGroup.service)
    )

  implicit val formatAccessGroupSummary: OFormat[GroupSummary] = Json.format[GroupSummary]
}

