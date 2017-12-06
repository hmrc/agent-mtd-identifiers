package uk.gov.hmrc.agentmtdidentifiers.model

import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}
import uk.gov.hmrc.agentmtdidentifiers.model.InvitationId._

class InvitationIdSpec extends FlatSpec with Matchers {
  val invWithoutPrefix = InvitationId.create(Arn("myAgency"), MtdItId("clientId"), "service", DateTime.parse("2001-01-01")) _

  "create" should "add prefix to start of identifier" in {
    invWithoutPrefix('A').value.head shouldBe 'A'
    invWithoutPrefix('B').value.head shouldBe 'B'
    invWithoutPrefix('C').value.head shouldBe 'C'
  }

  it should "create an identifier 10 characters long" in {
    invWithoutPrefix('A').value.length shouldBe 10
  }

  it should "append a CRC-5 alphanumeric checksum character" in {
    invWithoutPrefix('A').value.last shouldBe to5BitAlphaNumeric(CRC5.calculate("ABERULMHC"))
    invWithoutPrefix('B').value.last shouldBe to5BitAlphaNumeric(CRC5.calculate("BBERULMHC"))
    invWithoutPrefix('C').value.last shouldBe to5BitAlphaNumeric(CRC5.calculate("CBERULMHC"))
  }

  it should "give a different identifier whenever any of the arguments change" in {
    val agency = "agency"
    val clientId = "clientId"
    val service = "service"
    val time = DateTime.parse("2001-01-01")
    val prefix = 'A'

    val invA = InvitationId.create(Arn(agency), MtdItId(clientId), service, time)(prefix).value
    val invB = InvitationId.create(Arn("different"), MtdItId(clientId), service, time)(prefix).value
    val invC = InvitationId.create(Arn(agency), MtdItId("different"), service, time)(prefix).value
    val invD = InvitationId.create(Arn(agency), MtdItId(clientId), "different", time)(prefix).value
    val invE = InvitationId.create(Arn(agency), MtdItId(clientId), service, DateTime.parse("1999-01-01"))(prefix).value
    val invF = InvitationId.create(Arn(agency), MtdItId(clientId), service, DateTime.parse("1999-01-01"))('Z').value

    Set(invA, invB, invC, invD, invE, invF).size shouldBe 6
  }

  "byteToBitsLittleEndian" should "return little endian bit sequences (LSB first) for signed bytes" in {
    byteToBitsLittleEndian(0) shouldBe Seq(false, false, false, false, false, false, false, false)
    byteToBitsLittleEndian(1) shouldBe Seq(true, false, false, false, false, false, false, false)
    byteToBitsLittleEndian(2) shouldBe Seq(false, true, false, false, false, false, false, false)
    byteToBitsLittleEndian(3) shouldBe Seq(true, true, false, false, false, false, false, false)
    byteToBitsLittleEndian(127) shouldBe Seq(true, true, true, true, true, true, true, false)
    byteToBitsLittleEndian(-1) shouldBe Seq(true, true, true, true, true, true, true, true)
    byteToBitsLittleEndian(-2) shouldBe Seq(false, true, true, true, true, true, true, true)
    byteToBitsLittleEndian(-128) shouldBe Seq(false, false, false, false, false, false, false, true)
  }

  it should "return a unique bit sequence for all possible byte values" in {
    (-128 to 127).map(x => byteToBitsLittleEndian(x.toByte)).toSet.size shouldBe 256
  }

  "to5BitNum" should "return a 5 bit number representing a 5-bit bit sequence" in {
    to5BitNum(Seq(false, false, false, false, false)) shouldBe 0
    to5BitNum(Seq(true, false, false, false, false)) shouldBe 1
    to5BitNum(Seq(false, true, false, false, false)) shouldBe 2
    to5BitNum(Seq(true, true, true, true, true)) shouldBe 31
  }

  it should "return a unique number for all possible 5-bit bit sequences" in {
    (-128 to 127).map(x => to5BitNum(byteToBitsLittleEndian(x.toByte).take(5))).toSet.size shouldBe 32
  }

  it should "throw IllegalArgumentException if the passed sequence does not contain 5 bits" in {
    an[IllegalArgumentException] shouldBe thrownBy {
      to5BitNum(Seq.empty)
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      to5BitNum(Seq(true))
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      to5BitNum(Seq(true, true, true, true))
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      to5BitNum(Seq(true, true, true, true, true, true))
    }
  }

  "bytesTo5BitNums" should "return 8 5-bit numbers from of the bytes' bits" in {
    val ff = 0xFF.toByte
    bytesTo5BitNums(Seq(0, 0, 0, 0, 0)) shouldBe Seq(0, 0, 0, 0, 0, 0, 0, 0)
    bytesTo5BitNums(Seq(1, 0, 0, 0, 0)) shouldBe Seq(1, 0, 0, 0, 0, 0, 0, 0)
    bytesTo5BitNums(Seq(0x0FF.toByte, 0, 0, 0, 0)) shouldBe Seq(31, 7, 0, 0, 0, 0, 0, 0)
    bytesTo5BitNums(Seq(31, 0, 0, 0, 0)) shouldBe Seq(31, 0, 0, 0, 0, 0, 0, 0)
    bytesTo5BitNums(Seq(ff, ff, ff, ff, ff)) shouldBe Seq(31, 31, 31, 31, 31, 31, 31, 31)
  }

  it should "throw IllegalArgumentException if the passed sequence does not contain a multiple of 5 bytes" in {
    an[IllegalArgumentException] shouldBe thrownBy {
      bytesTo5BitNums(Seq.empty)
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      bytesTo5BitNums(Seq(1))
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      bytesTo5BitNums(Seq(1, 2, 3, 4))
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      bytesTo5BitNums(Seq(1, 2, 3, 4, 5, 6))
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      bytesTo5BitNums(Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
    }
  }

  "to5BitAlphaNumeric" should "return a unique character for each of the 32 values of the 5 bit number" in {
    (0 to 31).map(to5BitAlphaNumeric).mkString shouldBe "ABCDEFGHJKLMNOPRSTUWXYZ123456789"
  }

  it should "throw an IllegalArgumentException if the passed number is not within a 5 bit number's range" in {
    an[IllegalArgumentException] shouldBe thrownBy {
      to5BitAlphaNumeric(32)
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      to5BitAlphaNumeric(-1)
    }
  }
}
