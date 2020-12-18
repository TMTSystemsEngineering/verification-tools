package org.tmt.se.verification

import java.io.File

import org.tmt.se.verification.JiraInfo.VaApprovalValues
import org.tmt.se.verification.VerificationModels.{TestReportEntry, VAReport}
import spray.json.enrichAny

import scala.collection.immutable.HashMap

// https://tmt-project.atlassian.net/wiki/spaces/DEOPSCSW/pages/1230340111/RTM-report+v3.0.0#{storyId}
class JiraUpdater(jiraUser: String, jiraPw: String, project: String, reportUrl: String, reportFile: File) {
  def updateVaProceduresFromTestReport(): Unit = {
    // update Va Procedure in JIRA and print result
    createVaReportList(RtmManager.getTestResults(reportFile))
     .map(updateVaInfo).foreach(println)
  }

  def createVaReportList(testResults: List[TestReportEntry]): List[VAReport] = {
    var storyMap = HashMap[String, Boolean]()
    testResults.foreach {
      case TestReportEntry(Some(id), _, _, passed) => storyMap = storyMap + (id -> (storyMap.getOrElse(id, passed) && passed))
      case _ =>
    }
    storyMap
      .toList
      .map{case (storyId, passed) => VAReport(storyId, s"$reportUrl#$storyId", passed)}
  }

  def updateVaInfo(vaReport: VAReport): String = vaReport match {
    case VAReport(storyId, reportLink, passed) if storyId.startsWith(project) =>
      JiraAccess.updateIssueField(storyId, JiraInfo.FieldIds.VA_PROCEDURE_REPORT, JiraAccess.createVaProcedureReportJsonNoHref(reportLink), jiraUser, jiraPw)
      JiraAccess.updateIssueField(storyId, JiraInfo.FieldIds.VA_APPROVAL, passedString(passed).toJson.toString, jiraUser, jiraPw)
    case x => s"Skipping invalid story ID: ${x.storyId}"
  }

  private def passedString(passed: Boolean) = if (passed) VaApprovalValues.pass else VaApprovalValues.fail
}
