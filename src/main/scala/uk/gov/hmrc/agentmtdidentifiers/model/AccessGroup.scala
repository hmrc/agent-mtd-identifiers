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
