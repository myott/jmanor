import com.fasterxml.jackson.databind.ObjectMapper
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.test.http.TestHttpClient
import ratpack.test.http.TestHttpClients
import spock.lang.Specification

import static ratpack.http.MediaType.APPLICATION_JSON

class BasicTestSpec extends Specification {

  LocalScriptApplicationUnderTest aut = new LocalScriptApplicationUnderTest()

  @Delegate
  TestHttpClient client = TestHttpClients.testHttpClient(aut)

  def "Test an empty polling event"() {
    when:
    requestSpec {
      it.headers.add("Content-Type", APPLICATION_JSON)
      it.body.stream { it << "{}" }
    }
    get "poll"

    then:
    response.statusCode == 200

    and:
    def json = new ObjectMapper().reader().readTree(response.body.text)
    json.get("message").asText() == ""
    json.get("data").asText() == ""
    !json.get("error").asBoolean()
  }

  def "Test push notification on queue and test size"() {
    given:
    def jsonPayload = """{
  "profile" : 456,
  "name" : "study_invite",
  "value" : "post_id:123",
  "context" : "push_notification_service",
  "tags" : [
    {
      "name" : "new_study_invite_test",
      "value" : "_"
    },
    {
      "name" : "respondent_service",
      "value" : "paradigm"
    }
  ]
}"""

    def jsonPayload2 = """{
"sub": [
 {
   "name"  : "id",
   "value" : "456"
 }
]
}"""
    when:
    requestSpec {
      it.headers.add("Content-Type", APPLICATION_JSON)
      it.body.stream { it << jsonPayload }
    }
    post "push"

    then:
    requestSpec {
      it.headers.add("Content-Type", APPLICATION_JSON)
      it.body.stream { it << jsonPayload2 }
    }
    post "poll"

    then:
    response.statusCode == 200

  }





}

//{"profileId" : 456,"name" : "study_invite","value" : "post_id:123","insertContext" : "push_notification_service""tags" : [    {      "name" : "new_study_invite_test",      "value" : "_"    },    {      "name" : "respondent_service",      "value" : "paradigm"   }]}
//{
//  "profileId" : 456,
//  "name" : "study_invite",
//  "value" : "post_id:123",
//  "insertContext" : "push_notification_service"
//  "tags" : [
//    {
//      "name" : "new_study_invite_test",
//      "value" : "_"
//    },
//    {
//      "name" : "respondent_service",
//      "value" : "paradigm"
//    }
//  ]
//}
