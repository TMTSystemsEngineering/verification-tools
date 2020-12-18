package org.tmt.se.verification

import java.io.File

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.tmt.se.verification.VerificationModels.VAReport

import scala.io.Source

class JiraUpdaterTest extends AnyWordSpec with Matchers {
  private val user = sys.env.getOrElse("JIRAUSER", "")
  private val pw = sys.env.getOrElse("JIRATOKEN", "")
  private val link = "https://tmt-project.atlassian.net/wiki/spaces/DEOPSCSW/pages/1230340111/RTM-report+v3.0.0"
  val testReport = new File("jira-updater/src/test/resources/testRequirementsMapping.txt")
  val jiraUpdater = new JiraUpdater(user, pw, "DEOPSCSW", link, testReport)
  "Jira updater" should {
    "update va info in jira" in {
      // This test changes JIRA issue and is just used during development for testing
      //val vaReport = VAReport("DEOPSCSW-622", "https://tmt-project.atlassian.net/wiki/spaces/DEOPSCSW/pages/1230340111/RTM-report+v3.0.0#DEOPSCSW-622", passed = false)
      //jiraUpdater.updateVaInfo(vaReport)
    }
    "update all va info in jira" in {
      // This test changes many JIRA issues and is just used during development for testing
      println(testReport)
      //jiraUpdater.updateVaProceduresFromTestReport()
    }
    "create story pass list" in {
      val expectedResult = List(
        VAReport("CSW-97", "https://tmt-project.atlassian.net/wiki/spaces/DEOPSCSW/pages/1230340111/RTM-report+v3.0.0#CSW-97", passed = true),
        VAReport("DEOPSCSW-673", "https://tmt-project.atlassian.net/wiki/spaces/DEOPSCSW/pages/1230340111/RTM-report+v3.0.0#DEOPSCSW-673", passed = false),
        VAReport("DEOPSCSW-331", "https://tmt-project.atlassian.net/wiki/spaces/DEOPSCSW/pages/1230340111/RTM-report+v3.0.0#DEOPSCSW-331", passed = true),
        VAReport("DEOPSCSW-97", "https://tmt-project.atlassian.net/wiki/spaces/DEOPSCSW/pages/1230340111/RTM-report+v3.0.0#DEOPSCSW-97", passed = true)
      )
      jiraUpdater.createVaReportList(RtmManager.createTestReportList(Source.fromResource("SampleTestReport.csv").getLines())) shouldBe expectedResult

    }
    "create real story pass list" in {
      jiraUpdater.createVaReportList(RtmManager.createTestReportList(Source.fromResource("testRequirementsMapping.txt").getLines())).foreach(println)
    }
  }
}
