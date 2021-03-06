package org.tmt.se.verification

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, Uri, _}
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import org.tmt.se.verification.JiraInfo._
import spray.json.{JsArray, JsObject, JsValue, JsonParser, enrichAny}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor}

object JiraAccess {
  private val defaultUser: String = sys.env.getOrElse("JIRAUSER", "")
  private val defaultToken: String = sys.env.getOrElse("JIRATOKEN", "")

  val rootUrl = "https://tmt-project.atlassian.net/rest/api/3"

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  implicit class StringHelpers(s: String) {
    def stripStartAndEndQuotes(): String = s.replaceAll("^\"|\"$", "")
  }

  private def extractResponse(response: HttpResponse) =
    response.
      entity.
      dataBytes
      .runWith(Sink.fold(ByteString.empty)(_ ++ _))
      .map(_.utf8String)

  // todo return either
  def get(uri: Uri, user: String = defaultUser, pw: String = defaultToken): Option[String] = {
    val authHeader = headers.Authorization(BasicHttpCredentials(user, pw))
    val request  = HttpRequest(HttpMethods.GET, uri, headers=List(authHeader))
    val response = Await.result(Http(system).singleRequest(request), 20.seconds)

    if (response.status.isFailure()) {
      println(s"Error with JIRA request: URI = $uri")
      println(response.status.defaultMessage())
      None
    } else {
      Some(Await.result(extractResponse(response), 5.seconds))
    }
  }

  def put(uri: Uri, bodyJson: String, user: String = defaultUser, pw: String = defaultToken): String = {
    val authHeader = headers.Authorization(BasicHttpCredentials(user, pw))
    val contentType = headers.`Content-Type`(ContentTypes.`application/json`)
    val sanitizedBody = sanitizeBody(bodyJson)
    val entity = HttpEntity(ContentTypes.`application/json`, sanitizedBody)

    val request  = HttpRequest(HttpMethods.PUT, uri, headers=List(authHeader, contentType), entity = entity)
    println(request.toString())
    println(sanitizedBody)
    val response = Await.result(Http(system).singleRequest(request), 20.seconds)

    if (response.status.isFailure()) {
      val errorMsg = s"Error with JIRA request: URI = $uri, status code = ${response.status}, message=${response.status.defaultMessage()}"
      println(errorMsg)
      errorMsg
    } else {
      Await.result(extractResponse(response), 5.seconds)
    }
  }

  def getIssueField(storyId: String, fieldId: String, user: String = defaultUser, pw: String = defaultToken): Option[JsValue] = {
    val uri = s"$rootUrl/issue/$storyId"
    get(uri, user, pw).map(
      JsonParser(_)
        .asJsObject()
        .fields("fields")
        .asJsObject()
        .fields(fieldId)
    )
  }

  def getIssueVerifiesLinks(storyId: String, user: String = defaultUser, pw: String = defaultToken): Option[List[String]] = {
    def isIssueLinkAVerifies(o: JsObject) = o.fields("type").asJsObject().fields("id").toString().stripStartAndEndQuotes() == JiraInfo.IssueLinkTypes.VERIFIES
    getIssueField(storyId, JiraInfo.FieldIds.ISSUE_LINKS, user, pw).map(
      _.asInstanceOf[JsArray]
          .elements
          .map(_.asJsObject())
          .collect {
            case v if isIssueLinkAVerifies(v) =>
              v.fields("outwardIssue")
                .asJsObject()
                .fields("key")
                .toString()
                .stripStartAndEndQuotes()
          }.toList
    )
  }

  def updateIssueField(storyId: String, fieldId: String, newValue: String, user: String = defaultUser, pw: String = defaultToken): String = {
    val uri = s"$rootUrl/issue/$storyId"
    val update = Update(Field(fieldId, List(FieldData(newValue))))
    put(uri, update.toJson.toString, user, pw)
  }

  private def sanitizeBody(body: String) = body
    .replace("\\\"", "\"")
    .replace("\"{", "{")
    .replace("}\"", "}")

  def createVaProcedureReportJson(link: String): String = "{\"version\":1,\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\""+link+"\",\"marks\":[{\"type\":\"link\",\"attrs\":{\"href\":\""+link+"\"}}]}]}]}"
  def createVaProcedureReportJsonNoHref(link: String): String = "{\"version\":1,\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\""+link+"\"}]}]}"

}
