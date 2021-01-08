package org.tmt.se.verification

import java.io.File

import org.tmt.se.verification.JiraInfo.VaApprovalValues
import org.tmt.se.verification.VerificationModels.{ReqVAReport, TestReportEntry, VAReport}
import spray.json.enrichAny

import scala.collection.immutable.HashMap

// https://tmt-project.atlassian.net/wiki/spaces/DEOPSCSW/pages/1230340111/RTM-report+v3.0.0#{storyId}
class JiraUpdater(jiraUser: String, jiraPw: String, project: String, reportUrl: String, reportFile: File) {
  def updateVaProceduresFromTestReport(): Unit = {
    // update Va Procedure in JIRA and print result
    createVaReportList(RtmManager.getTestResults(reportFile))
     .map(updateStoryVaInfo).foreach(println)
  }
  def updateReqVaProceduresFromTestReport(): Unit = {
    // update Va Procedure in JIRA and print result
    createReqVaReportList(RtmManager.getTestResults(reportFile))
      .map(updateReqVaInfo).foreach(println)
  }


  def createStoryMap(testResults: List[TestReportEntry]): HashMap[String, Boolean] = {
    var storyMap = HashMap[String, Boolean]()
    testResults.foreach {
      case TestReportEntry(Some(id), _, _, passed) if id.startsWith("DEOPSCSW") => storyMap = storyMap + (id -> (storyMap.getOrElse(id, passed) && passed))
      case _ =>
    }
    storyMap
  }
  def createVaReportList(testResults: List[TestReportEntry]): List[VAReport] = {
    createStoryMap(testResults)
      .toList
      .map{case (storyId, passed) => VAReport(storyId, createReportLink(storyId), passed)}
  }
  def createReqVaReportList(testResults: List[TestReportEntry]): List[ReqVAReport] = {
    val storyMap = createStoryMap(testResults)
    var storyToReqMap = HashMap[String, List[String]]()
    storyMap.toList.foreach {
      case (storyId, _) =>
        //println(storyId)
        JiraAccess.getIssueVerifiesLinks(storyId, jiraUser, jiraPw).foreach { newStories =>
          //println(newStories)
          storyToReqMap = storyToReqMap + (storyId -> newStories)
        }
    }
    // invert map
    val reqToStoryMap = invertMap(storyToReqMap)
    reqToStoryMap.toList.map {
      case (reqId, storyIds) => ReqVAReport(reqId, storyIds.map(createReportLink), storyIds.flatMap(storyMap.get).foldLeft(true)(_ && _))
    }
  }

  def invertMap[A,B](map: Map[A,Iterable[B]]): Map[B, List[A]] = {
    map
      .toList
      .flatMap { case (a, b) => b.map(_ -> a) }
      .groupBy(_._1)
      .view
      .mapValues(_.map(_._2))
      .toMap
  }

  def updateStoryVaInfo(vaReport: VAReport): String = vaReport match {
    case VAReport(storyId, reportLink, passed) if storyId.startsWith(project) =>
      JiraAccess.updateIssueField(storyId, JiraInfo.FieldIds.VA_PROCEDURE_REPORT, JiraAccess.createVaProcedureReportJsonNoHref(reportLink), jiraUser, jiraPw)
      JiraAccess.updateIssueField(storyId, JiraInfo.FieldIds.VA_APPROVAL, passedValue(passed).toJson.toString, jiraUser, jiraPw)
    case x => s"Skipping invalid story ID: ${x.storyId}"
  }

  def updateReqVaInfo(vaReport: ReqVAReport): String = vaReport match {
    case ReqVAReport(reqIssueId, reportLinks, passed) if reqIssueId.startsWith("VER") =>
      JiraAccess.updateIssueField(reqIssueId, JiraInfo.FieldIds.VA_PROCEDURE_REPORT, JiraAccess.createVaProcedureReportJsonNoHref(reportLinks.mkString("\n")), jiraUser, jiraPw)
      JiraAccess.updateIssueField(reqIssueId, JiraInfo.FieldIds.VA_APPROVAL, passedValue(passed).toJson.toString, jiraUser, jiraPw)
    case x => s"Skipping invalid story ID: ${x.reqIssueId}"
  }

  private def passedValue(passed: Boolean) = if (passed) VaApprovalValues.pass else VaApprovalValues.fail
  private def createReportLink(storyId: String) = s"$reportUrl#$storyId"
}

