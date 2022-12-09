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

import play.api.libs.json._

case class AccessGroupSummary(groupId: String, groupName: String, clientCount: Int, teamMemberCount: Int, isCustomGroup: Boolean)

object AccessGroupSummary {

  def convertCustom(accessGroup: AccessGroup): AccessGroupSummary =
    AccessGroupSummary(
      accessGroup._id.toHexString,
      accessGroup.groupName,
      accessGroup.clients.fold(0)(_.size),
      accessGroup.teamMembers.fold(0)(_.size),
      isCustomGroup = true
    )

  def convertTaxServiceGroup(accessGroup: TaxServiceAccessGroup): AccessGroupSummary =
    AccessGroupSummary(
      accessGroup._id.toHexString,
      accessGroup.groupName,
      0, // info is obtained elsewhere, custom groups should never have less than 1 client
      accessGroup.teamMembers.fold(0)(_.size),
      isCustomGroup = false // could use client count but this is more explicit
    )

  implicit val formatAccessGroupSummary: OFormat[AccessGroupSummary] = Json.format[AccessGroupSummary]
}

