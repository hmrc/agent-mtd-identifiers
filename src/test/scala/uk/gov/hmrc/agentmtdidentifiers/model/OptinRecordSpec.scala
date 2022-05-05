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

import org.scalatest.{Matchers, WordSpecLike}

import java.time.LocalDateTime

class OptinRecordSpec extends WordSpecLike with Matchers {

  s"OptinRecord status" when {

    val arn = Arn("KARN1234567")
    val user = AgentUser("userId", "userName")

    "no events exist" should {

      s"be $OptedOut" in {
        withOptinRecord(List.empty).status shouldBe OptedOut
      }
    }

    "only a single opted event exists" when {

      s"has status $OptedIn" should {
        s"be $OptedIn" in {
          withOptinRecord(List(OptedIn -> LocalDateTime.now())).status shouldBe OptedIn
        }
      }

      s"has status $OptedOut" should {
        s"be $OptedOut" in {
          withOptinRecord(List(OptedOut -> LocalDateTime.now())).status shouldBe OptedOut
        }
      }
    }

    "multiple opted events exist" when {

      s"has latest $OptedIn event" should {
        s"be $OptedIn" in {
          val now = LocalDateTime.now()

          withOptinRecord(
            List(OptedIn -> now.minusDays(1), OptedOut -> now.minusSeconds(1), OptedIn -> now)
          ).status shouldBe OptedIn
        }
      }

      s"has latest $OptedOut event" should {
        s"be $OptedOut" in {
          val now = LocalDateTime.now()

          withOptinRecord(
            List(OptedOut -> now.minusDays(1), OptedIn -> now.minusNanos(1000), OptedOut -> now)
          ).status shouldBe OptedOut
        }
      }
    }

    def withOptinRecord(mapStatusToEpoch: List[(OptinEventType, LocalDateTime)]): OptinRecord =
      OptinRecord(arn, mapStatusToEpoch.map { case (optedStatus, epoch) => OptinEvent(optedStatus, user, epoch) })

  }
}
