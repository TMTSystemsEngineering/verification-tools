package org.tmt.se.verification

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.tmt.se.verification.VerificationModels.TestReportEntry

import scala.io.Source

class RtmManagerTest extends AnyWordSpec with Matchers {

  "RtmManager" should {
    "parse csv to list of TestReportEntry's" in {
      val source = Source.fromResource("SampleTestReport.csv")

      val expectedResult = List(
        TestReportEntry(Some("DEOPSCSW-331"), Some("REQ-2-CSW-3035"), "test ByteArrayKey should able to create parameter representing binary image", passed = true),
        TestReportEntry(None, None, "should return true when server is running on given host and port", passed = true),
        TestReportEntry(None, None, "should throw exception when server is not running on given host and port", passed = true),
        TestReportEntry(Some("DEOPSCSW-673"), None, "Networks() should throw NetworkInterfaceNotProvided when INTERFACE_NAME env variable is not set", passed = false),
        TestReportEntry(Some("DEOPSCSW-673"), None, "Networks() should throw NetworkInterfaceNotProvided when INTERFACE_NAME2 env variable is not set", passed = true),
        TestReportEntry(Some("DEOPSCSW-97"), None, "Networks() should throw NetworkInterfaceNotProvided when INTERFACE_NAME env variable is not set", passed = true),
        TestReportEntry(Some("DEOPSCSW-97"), None, "Networks() should throw NetworkInterfaceNotProvided when INTERFACE_NAME2 env variable is not set", passed = true),
        TestReportEntry(Some("CSW-97"), None, "Networks(some-interface-name) should throw NetworkInterfaceNotProvided when provided interface name env variable is not set", passed = true)
      )

      RtmManager.createTestReportList(source.getLines()) shouldBe expectedResult
    }
  }
}
