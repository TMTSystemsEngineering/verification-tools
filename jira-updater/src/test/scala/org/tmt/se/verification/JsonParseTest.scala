package org.tmt.se.verification

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.{JsonParser, _}

import scala.io.Source

class JsonParseTest extends AnyWordSpec with Matchers{

  import JiraInfo._

  "json parser" should {
    "parse json to object" in {
      val sampleJson = JsonParser(Source.fromResource("SampleIssue-DEOPSCSW-622.json").mkString)
      val expectedJson = "{\"content\":[{\"attrs\":{\"isNumberColumnEnabled\":false,\"layout\":\"default\"},\"content\":[{\"content\":[{\"attrs\":{},\"content\":[{\"content\":[{\"text\":\"Release Version \",\"type\":\"text\"}],\"type\":\"paragraph\"}],\"type\":\"tableHeader\"},{\"attrs\":{},\"content\":[{\"content\":[{\"text\":\"Link to Test Report \",\"type\":\"text\"}],\"type\":\"paragraph\"}],\"type\":\"tableHeader\"},{\"attrs\":{},\"content\":[{\"content\":[{\"text\":\"Pass / Fail \",\"type\":\"text\"}],\"type\":\"paragraph\"}],\"type\":\"tableHeader\"}],\"type\":\"tableRow\"},{\"content\":[{\"attrs\":{},\"content\":[{\"content\":[],\"type\":\"paragraph\"}],\"type\":\"tableCell\"},{\"attrs\":{},\"content\":[{\"content\":[],\"type\":\"paragraph\"}],\"type\":\"tableCell\"},{\"attrs\":{},\"content\":[{\"content\":[],\"type\":\"paragraph\"}],\"type\":\"tableCell\"}],\"type\":\"tableRow\"}],\"type\":\"table\"}],\"type\":\"doc\",\"version\":1}"

      val fields = sampleJson.asJsObject.fields("fields")
      fields.asJsObject.fields("customfield_12048").toString() shouldBe expectedJson

    }
    "parse update" in {
      val testField = Field("myName", List(FieldData("test")))
      val testFieldJson = "{\"myName\":[{\"set\":\"test\"}]}"
      val testUpdate = Update(Field("myName", List(FieldData("test"))))
      val testUpdateJson = "{\"update\":{\"myName\":[{\"set\":\"test\"}]}}"
      testField.toJson.toString shouldBe testFieldJson
      testFieldJson.parseJson.convertTo[Field] shouldBe testField
      testUpdate.toJson.toString shouldBe testUpdateJson
      testUpdateJson.parseJson.convertTo[Update] shouldBe testUpdate
    }

  }


}
