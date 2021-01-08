package org.tmt.se.verification

object VerificationModels {
  case class TestReportEntry(storyId: Option[String], requirement: Option[String], testName: String, passed: Boolean)
  case class VAReport(storyId: String, reportLink: String, passed: Boolean)
  case class ReqVAReport(reqIssueId: String, reportLinks: List[String], passed: Boolean)

  case class RegressionTestInfo(version: String, reportLink: String, passFail: String)
  case class RegressionTestingHistory(history: List[RegressionTestInfo])

}
