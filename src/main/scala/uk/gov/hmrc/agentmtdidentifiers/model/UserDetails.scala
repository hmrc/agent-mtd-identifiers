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

import play.api.libs.json.{Format, Json, OFormat}

case class AgentUser(id: String, name: String)

object AgentUser {
  implicit val formatAgentUser: OFormat[AgentUser] = Json.format[AgentUser]
}

/**
 * Cut down version of UserDetails from users-groups-search.
 * Contains only the fields we are interested in.
 * */
case class UserDetails(userId: Option[String] = None,
                       credentialRole: Option[String] = None,
                       name: Option[String] = None,
                       email: Option[String] = None)

object UserDetails {

  def fromAgentUser(agentUser: AgentUser): UserDetails =
    UserDetails(userId = Some(agentUser.id), name = Some(agentUser.name))

  implicit val formats: Format[UserDetails] = Json.format
}
