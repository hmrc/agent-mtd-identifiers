package uk.gov.hmrc.newmodel

import uk.gov.hmrc.agentmtdidentifiers.model.{CbcId, MtdItId, Utr, Vrn}

object Demo extends App {
  val mtdItId: MtdItId = MtdItId("8HW56jIbk830PbK")
  val vrn: Vrn = Vrn("109354956")
  val cbcId: CbcId = CbcId("XLCBC0003763895")
  val utr: Utr = Utr("7493478251")

  val enrolmentKey2 = HmrcMtdVat.makeEnrolmentKey(vrn)

  // val enrolmentKey1e = HmrcMtdIt.makeEk(vrn) // incorrect identifiers - doesn't compile
  val enrolmentKey1 = HmrcMtdIt.makeEnrolmentKey(mtdItId) // correct identifiers - compiles

  // val enrolmentKey3e = HmrcCbcOrg.makeEk(cbcId) // incorrect identifiers - doesn't compile
  val enrolmentKey3 = HmrcCbcOrg.makeEnrolmentKey((cbcId, utr)) // correct identifiers - compiles

  // val enrolmentKey4e = HmrcCbcNonukOrg.makeEk((cbcId, utr)) // incorrect identifiers - doesn't compile
  val enrolmentKey4 = HmrcCbcNonukOrg.makeEnrolmentKey(cbcId) // correct identifiers - compiles

  println(enrolmentKey1)
  println(enrolmentKey2)
  println(enrolmentKey3)
  println(enrolmentKey4)
}
