package org.tmt.se.verification

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.tmt.se.verification.JiraInfo.VaApprovalValues
import spray.json.enrichAny

class JiraAccessTest extends AnyWordSpec with Matchers {

  private val user = sys.env.getOrElse("JIRAUSER", "")
  private val pw = sys.env.getOrElse("JIRATOKEN", "")

  "JIRA Access" should {
    "get return list of Issue Link Types on request" in {

      val expectedOutput = "" +
        "{\"issueLinkTypes\":[{\"id\":\"10000\",\"name\":\"Blocks\",\"inward\":\"is blocked by\",\"outward\":\"blocks\",\"self\":\"https://tmt-project.atlassian.net/rest/api/3/issueLinkType/10000\"},{\"id\":\"10001\",\"name\":\"Cloners\",\"inward\":\"is cloned by\",\"outward\":\"clones\",\"self\":\"https://tmt-project.atlassian.net/rest/api/3/issueLinkType/10001\"},{\"id\":\"10002\",\"name\":\"Duplicate\",\"inward\":\"is duplicated by\",\"outward\":\"duplicates\",\"self\":\"https://tmt-project.atlassian.net/rest/api/3/issueLinkType/10002\"},{\"id\":\"10401\",\"name\":\"Gantt End to End\",\"inward\":\"has to be finished together with\",\"outward\":\"has to be finished together with\",\"self\":\"https://tmt-project.atlassian.net/rest/api/3/issueLinkType/10401\"},{\"id\":\"10402\",\"name\":\"Gantt End to Start\",\"inward\":\"has to be done after\",\"outward\":\"has to be done before\",\"self\":\"https://tmt-project.atlassian.net/rest/api/3/issueLinkType/10402\"},{\"id\":\"10403\",\"name\":\"Gantt Start to End\",\"inward\":\"start is earliest end of\",\"outward\":\"earliest end is start of\",\"self\":\"https://tmt-project.atlassian.net/rest/api/3/issueLinkType/10403\"},{\"id\":\"10404\",\"name\":\"Gantt Start to Start\",\"inward\":\"has to be started together with\",\"outward\":\"has to be started together with\",\"self\":\"https://tmt-project.atlassian.net/rest/api/3/issueLinkType/10404\"},{\"id\":\"10300\",\"name\":\"Problem/Incident\",\"inward\":\"is caused by\",\"outward\":\"causes\",\"self\":\"https://tmt-project.atlassian.net/rest/api/3/issueLinkType/10300\"},{\"id\":\"10003\",\"name\":\"Relates\",\"inward\":\"relates to\",\"outward\":\"relates to\",\"self\":\"https://tmt-project.atlassian.net/rest/api/3/issueLinkType/10003\"},{\"id\":\"10400\",\"name\":\"Verify\",\"inward\":\"is verified by\",\"outward\":\"verifies\",\"self\":\"https://tmt-project.atlassian.net/rest/api/3/issueLinkType/10400\"}]}"

      val uri = "https://tmt-project.atlassian.net/rest/api/3/issueLinkType"
      JiraAccess.get(uri, user, pw) shouldBe expectedOutput
    }
    "get issue field should return just the field" in {
      val expectedJson = "{\"content\":[{\"attrs\":{\"isNumberColumnEnabled\":false,\"layout\":\"default\"},\"content\":[{\"content\":[{\"attrs\":{},\"content\":[{\"content\":[{\"text\":\"Release Version \",\"type\":\"text\"}],\"type\":\"paragraph\"}],\"type\":\"tableHeader\"},{\"attrs\":{},\"content\":[{\"content\":[{\"text\":\"Link to Test Report \",\"type\":\"text\"}],\"type\":\"paragraph\"}],\"type\":\"tableHeader\"},{\"attrs\":{},\"content\":[{\"content\":[{\"text\":\"Pass / Fail \",\"type\":\"text\"}],\"type\":\"paragraph\"}],\"type\":\"tableHeader\"}],\"type\":\"tableRow\"},{\"content\":[{\"attrs\":{},\"content\":[{\"content\":[],\"type\":\"paragraph\"}],\"type\":\"tableCell\"},{\"attrs\":{},\"content\":[{\"content\":[],\"type\":\"paragraph\"}],\"type\":\"tableCell\"},{\"attrs\":{},\"content\":[{\"content\":[],\"type\":\"paragraph\"}],\"type\":\"tableCell\"}],\"type\":\"tableRow\"}],\"type\":\"table\"}],\"type\":\"doc\",\"version\":1}"
      JiraAccess.getIssueField("DEOPSCSW-622", JiraInfo.FieldIds.REGRESSION_TESTING_HISTORY, user, pw).toString() shouldBe expectedJson
    }
    "update VA ID" in {
      // this actually updates a field, and probably shouldn't be used unless you know what you are doing.
      JiraAccess.updateIssueField("DEOPSCSW-622", "customfield_11932", "test3", user, pw)
    }

    "update VA procedure" in {
      // this actually updates a field, and probably shouldn't be used unless you know what you are doing.
      JiraAccess.updateIssueField("DEOPSCSW-622", JiraInfo.FieldIds.VA_PROCEDURE_REPORT, JiraAccess.createVaProcedureReportJson("https://docushare.tmt.org/docushare/dsweb/Get/Document-79767/TestReport_CSW_v1.0.0-RC4_20190828_final.txt"), user, pw)
    }

    "create va procedure json" in {
      val expectedJson = "{\"version\":1,\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"https://docushare.tmt.org/docushare/dsweb/Get/Document-79767/TestReport_CSW_v1.0.0-RC4_20190828_final.txt\",\"marks\":[{\"type\":\"link\",\"attrs\":{\"href\":\"https://docushare.tmt.org/docushare/dsweb/Get/Document-79767/TestReport_CSW_v1.0.0-RC4_20190828_final.txt\"}}]}]}]}"
      println(expectedJson)
      JiraAccess.createVaProcedureReportJson("https://docushare.tmt.org/docushare/dsweb/Get/Document-79767/TestReport_CSW_v1.0.0-RC4_20190828_final.txt") shouldBe expectedJson
    }
    "VaApproval json" in {
      val pass = VaApprovalValues.pass
      println(pass.toJson)

    }
  }
}
/// https://docushare.tmt.org/docushare/dsweb/Get/Document-79767/TestReport_CSW_v1.0.0-RC4_20190828_final.txt