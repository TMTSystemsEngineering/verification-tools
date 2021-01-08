package org.tmt.se.verification

import spray.json.DefaultJsonProtocol.{jsonFormat1, listFormat, _}
import spray.json.{JsObject, JsValue, RootJsonFormat, _}

case object JiraInfo {

  case object FieldIds {
    val REGRESSION_TESTING_HISTORY: String = "customfield_12048"
    val VA_PROCEDURE_REPORT: String = "customfield_11999"
    val VA_APPROVAL: String = "customfield_11998"
    val ISSUE_LINKS: String = "issuelinks"
  }
  case object IssueLinkTypes {
    val VERIFIES = "10400"
  }


  case class Update(field: Field)
  case class Field(name: String, data: List[FieldData])
  case class FieldData(set:String)
  implicit val fieldFormat: RootJsonFormat[FieldData] = jsonFormat1(FieldData)

  implicit object FieldFormat extends RootJsonFormat[Field] {
    def write(f: Field): JsValue = JsObject(f.name -> f.data.toJson)
    def read(value: JsValue): Field = {
      val fields = value.asJsObject().fields
      Field(fields.head._1, fields.head._2.convertTo[List[FieldData]])
    }
  }

  implicit object UpdateFormat extends RootJsonFormat[Update] {
    def write(u: Update): JsValue = JsObject("update" -> u.field.toJson)
    def read(value: JsValue): Update = {
      val fields = value.asJsObject().fields
      Update(fields("update").convertTo[Field])
    }
  }


  // the classes below are for Regression Testing History, but don't have proper JSON encoding yet, so would
  // need some work.
  case class Attr[T](name: String, value:T)
  class Content(val attrs: Option[List[Attr[_]]], val content: Option[List[Content]])

  case class table(override val attrs: Option[List[Attr[_]]], override val content: Option[List[Content]]) extends Content(attrs, content)
  case class tableRow(override val content: Option[List[Content]]) extends Content(None, content)
  case class tableHeader(override val attrs: Option[List[Attr[_]]], override val content: Option[List[Content]]) extends Content(attrs, content)
  case class tableCell(override val attrs: Option[List[Attr[_]]], override val content: Option[List[Content]]) extends Content(attrs, content)
  case class paragraph(override val content: Option[List[Content]]) extends Content(None, content)
  case class text(text: String) extends Content(None, None)
  // end Regression classes

  case class VaApprovalAllowedValue(self: String, value: String, id: String)
  case object VaApprovalValues {
    val none: VaApprovalAllowedValue = VaApprovalAllowedValue("https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11276", "--", "11276")
    val pass: VaApprovalAllowedValue = VaApprovalAllowedValue("https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11274", "PASS", "11274")
    val fail: VaApprovalAllowedValue = VaApprovalAllowedValue("https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11275", "FAIL", "11275")
    val c: VaApprovalAllowedValue = VaApprovalAllowedValue("https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11423", "C", "11423")
    val nc: VaApprovalAllowedValue = VaApprovalAllowedValue("https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11424", "NC", "11424")
    val tbd: VaApprovalAllowedValue = VaApprovalAllowedValue("https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11425", "TBD", "11425")
  }
  implicit val vaApprovalFormat: RootJsonFormat[VaApprovalAllowedValue] = jsonFormat3(VaApprovalAllowedValue)

  /* VA APPROVAL ALLOWED VALUES
           "allowedValues": [
                {
                    "self": "https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11276",
                    "value": "--",
                    "id": "11276"
                },
                {
                    "self": "https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11274",
                    "value": "Pass",
                    "id": "11274"
                },
                {
                    "self": "https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11275",
                    "value": "Fail",
                    "id": "11275"
                },
                {
                    "self": "https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11423",
                    "value": "C",
                    "id": "11423"
                },
                {
                    "self": "https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11424",
                    "value": "NC",
                    "id": "11424"
                },
                {
                    "self": "https://tmt-project.atlassian.net/rest/api/3/customFieldOption/11425",
                    "value": "TBD",
                    "id": "11425"
                }
            ]
 */

}
