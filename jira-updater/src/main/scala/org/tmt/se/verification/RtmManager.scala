package org.tmt.se.verification

import java.io.File

import org.tmt.se.verification.VerificationModels.TestReportEntry

import scala.io.Source

object RtmManager {
  private val delim = "\\|"

  def getTestResults(testResultFile: File): List[TestReportEntry] = {
    val source = Source.fromFile(testResultFile)

    createTestReportList(source.getLines)
  }

  def createTestReportList(lines: Iterator[String]): List[TestReportEntry] = lines.map(parseLine).toList.flatten

  private def parseLine(line: String): Option[TestReportEntry] = {
    //println(line)
    val parts = line.split(delim).map(_.trim)
    if (parts.length != 4)
      None
    else {
      def toOption(s: String) = s match {
        case "None" => None
        case x => Some(x.takeWhile(_ != '('))   // test report for CSW has some story ids as DEOPSCSW-XXX(Kafka) or DEOPSCSW-XXX(Redis)
      }
      def toBoolean(s: String) = s == "PASSED"

      val storyId = toOption(parts(0))
      val requirement = toOption(parts(1))
      val testName = parts(2)
      val passed = toBoolean(parts(3))
      Some(TestReportEntry(storyId, requirement, testName, passed))
    }
  }
}
