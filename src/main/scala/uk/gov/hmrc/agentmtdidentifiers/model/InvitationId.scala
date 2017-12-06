package uk.gov.hmrc.agentmtdidentifiers.model

import java.security.MessageDigest

import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json.{Format, __}
import play.api.libs.functional.syntax._

case class InvitationId(value: String) {
  require(value.size == 10, "The size of invitation id should not exceed 10")
}

object InvitationId {

  private def idWrites = (__ \ "value")
    .write[String]
    .contramap((id: InvitationId) => id.value.toString)

  private def idReads = (__ \ "value")
    .read[String]
    .map(x => InvitationId(x))

  implicit val idFormats = Format(idReads, idWrites)

  private[model] def byteToBitsLittleEndian(byte: Byte): Seq[Boolean] = {
    def isBitOn(bitPos: Int): Boolean = {
      val maskSingleBit = 0x01 << bitPos
      (byte & maskSingleBit) != 0
    }

    (0 to 7).map(isBitOn)
  }

  private[model] def to5BitNum(bitsLittleEndian: Seq[Boolean]) = {
    require(bitsLittleEndian.size == 5)

    bitsLittleEndian
      .take(5)
      .zipWithIndex
      .map{ case (bit, power) => if(bit) 1 << power else 0 }
      .sum
  }

  private[model] def bytesTo5BitNums(bytes: Seq[Byte]): Seq[Int] = {
    require(bytes.size % 5 == 0)
    require(bytes.nonEmpty)

    bytes.flatMap(byteToBitsLittleEndian).grouped(5).map(to5BitNum).toSeq
  }

  private[model] def to5BitAlphaNumeric(fiveBitNum: Int) = {
    require(fiveBitNum >= 0 && fiveBitNum <= 31)

    "ABCDEFGHJKLMNOPRSTUWXYZ123456789"(fiveBitNum)
  }

  def create(arn: Arn,
             clientId: MtdItId,
             serviceName: String,
             timestamp: DateTime = DateTime.now(DateTimeZone.UTC))(prefix: Char): InvitationId = {
    val idUnhashed = s"${arn.value}.${clientId.value},$serviceName-${timestamp.getMillis}"
    val idBytes = MessageDigest.getInstance("SHA-256").digest(idUnhashed.getBytes("UTF-8")).take(5)
    val idChars = bytesTo5BitNums(idBytes).map(to5BitAlphaNumeric).mkString
    val idWithPrefix = s"$prefix$idChars"
    val checkDigit = to5BitAlphaNumeric(CRC5.calculate(idWithPrefix))

    InvitationId(s"$idWithPrefix$checkDigit")
  }
}

private[model] object CRC5 {

  /* Params for CRC-5/EPC */
  val bitWidth = 5
  val poly = 0x09
  val initial = 0x09
  val xorOut = 0

  val table: Seq[Int] = {
    val widthMask = (1 << bitWidth) - 1
    val shpoly = poly << (8 - bitWidth)
    for (i <- 0 until 256) yield {
      var crc = i
      for (_ <- 0 until 8) {
        crc = if ((crc & 0x80) != 0) (crc << 1) ^ shpoly else crc << 1
      }
      (crc >> (8 - bitWidth)) & widthMask
    }
  }

  def calculate(string: String): Int = calculate(string.getBytes())

  def calculate(input: Array[Byte]): Int = {
    val start = 0
    val length = input.length
    var crc = initial ^ xorOut
    for (i <- 0 until length) {
      crc = table((crc << (8 - bitWidth)) ^ (input(start + i) & 0xff)) & 0xff
    }
    crc ^ xorOut
  }
}
