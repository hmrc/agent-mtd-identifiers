package uk.gov.hmrc.agentmtdidentifiers.model

import java.security.MessageDigest

import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, __}

case class InvitationId(value: String) {
  require(value.size == 19, "The size of invitation id should not exceed 19")
}

object InvitationId {

  private def idWrites = (__ \ "value")
    .write[String]
    .contramap((id: InvitationId) => id.value.toString)

  private def idReads = (__ \ "value")
    .read[String]
    .map(x => InvitationId(x))

  implicit val idFormats = Format(idReads, idWrites)

  def create(arn: Arn,
             clientId: MtdItId,
             serviceName: String,
             timestamp: DateTime = DateTime.now(DateTimeZone.UTC))(implicit prefix: Char): InvitationId = {
    val idUnhashed = s"${arn.value}.${clientId.value},$serviceName-${timestamp.getMillis}"
    val idBytes = MessageDigest.getInstance("SHA-256").digest(idUnhashed.getBytes("UTF-8")).take(10)
    val idChars = bytesTo5BitNums(idBytes).map(to5BitAlphaNumeric).mkString
    val idWithPrefix = s"$prefix$idChars"

    InvitationId(s"$idWithPrefix${checksumDigits(idWithPrefix)}")
  }

  private[model] def checksumDigits(toChecksum: String) = {
    val checksum10Bits = CRC10.calculate(toChecksum)
    val lsb5BitsChecksum = to5BitAlphaNumeric( checksum10Bits & 0x1F )
    val msb5BitsChecksum = to5BitAlphaNumeric( (checksum10Bits & 0x3E0) >> 5 )

    s"$lsb5BitsChecksum$msb5BitsChecksum"
  }

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
    require(bytes.size % 10 == 0)
    require(bytes.nonEmpty)

    bytes.flatMap(byteToBitsLittleEndian).grouped(5).map(to5BitNum).toSeq
  }

  private[model] def to5BitAlphaNumeric(fiveBitNum: Int) = {
    require(fiveBitNum >= 0 && fiveBitNum <= 31)

    "ABCDEFGHJKLMNOPRSTUWXYZ123456789"(fiveBitNum)
  }
}

private[model] object CRC10 {

  /* Params for CRC-10 */
  val bitWidth = 10
  val poly = 0x233
  val initial = 0
  val xorOut = 0
  val widthMask = (1 << bitWidth) - 1

  val table: Seq[Int] = {

    val top = 1 << (bitWidth - 1)

    for (i <- 0 until 256) yield {
      var crc = i << (bitWidth - 8)
      for (_ <- 0 until 8) {
        crc = if ((crc & top) != 0) (crc << 1) ^ poly else crc << 1
      }

      crc & widthMask
    }
  }

  def calculate(string: String): Int = calculate(string.getBytes())

  def calculate(input: Array[Byte]): Int = {
    val length = input.length
    var crc = initial ^ xorOut

    for (i <- 0 until length) {
      crc = table(((crc >>> (bitWidth - 8)) ^ input(i)) & 0xff) ^ (crc << 8)
    }
    crc & widthMask
  }
}
