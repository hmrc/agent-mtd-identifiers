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
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.{JsSuccess, Json}

import java.time.LocalDateTime

class AccessGroupSpec extends FlatSpec with Matchers {

    "AccessGroup" should "serialise to JSON and deserialize from string" in {
      val arn: Arn = Arn("KARN1234567")
      val groupName = "some group"
      val agent: AgentUser = AgentUser("userId", "userName")
      val user1: AgentUser = AgentUser("user1", "User 1")
      val user2: AgentUser = AgentUser("user2", "User 2")

      val client1: Client = Client("HMRC-MTD-VAT~VRN~101747641", "John Innes")
      val client2: Client = Client("HMRC-PPT-ORG~EtmpRegistrationNumber~XAPPT0000012345", "Frank Wright")
      val client3: Client = Client("HMRC-CGT-PD~CgtRef~XMCGTP123456789", "George Candy")

      val now = LocalDateTime.now()

      val id = new ObjectId()

      val accessGroup: AccessGroup =
        AccessGroup(
          id,
          arn,
          groupName,
          now,
          now,
          agent,
          agent,
          Some(Set(agent, user1, user2)),
          Some(Set(client1, client2, client3)),
          None,
          None
        )

      val jsonString =
        s"""{"_id":"${id.toHexString}","arn":"KARN1234567","groupName":"some group","created":"$now","lastUpdated":"$now","createdBy":{"id":"userId","name":"userName"},"lastUpdatedBy":{"id":"userId","name":"userName"},"teamMembers":[{"id":"userId","name":"userName"},{"id":"user1","name":"User 1"},{"id":"user2","name":"User 2"}],"clients":[{"enrolmentKey":"HMRC-MTD-VAT~VRN~101747641","friendlyName":"John Innes"},{"enrolmentKey":"HMRC-PPT-ORG~EtmpRegistrationNumber~XAPPT0000012345","friendlyName":"Frank Wright"},{"enrolmentKey":"HMRC-CGT-PD~CgtRef~XMCGTP123456789","friendlyName":"George Candy"}]}""".stripMargin

      Json.toJson(accessGroup).toString shouldBe jsonString
      Json.fromJson[AccessGroup](Json.parse(jsonString)) shouldBe JsSuccess(accessGroup)
    }


}
