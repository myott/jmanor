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
    when:
    requestSpec {
      it.headers.add("Content-Type", APPLICATION_JSON)
      it.body.stream { it << "{\n" +
          "  \"profile\" : 456,\n" +
          "  \"name\" : \"study_invite\",\n" +
          "  \"value\" : \"post_id:123\",\n" +
          "  \"context\" : \"push_notification_service\",\n" +
          "  \"tags\" : [\n" +
          "    {\n" +
          "      \"name\" : \"new_study_invite_test\",\n" +
          "      \"value\" : \"_\"\n" +
          "    },\n" +
          "    {\n" +
          "      \"name\" : \"respondent_service\",\n" +
          "      \"value\" : \"paradigm\"\n" +
          "    }\n" +
          "  ]\n" +
          "}" }
    }
    post "push"

    then:
    requestSpec {
      it.headers.add("Content-Type", APPLICATION_JSON)
      it.body.stream { it << "{\n" +
          "    \"sub\": [\n" +
          "        {\n" +
          "          \"name\" : \"id\",\n" +
          "          \"value\" : \"456\"\n" +
          "        }\n" +
          "    ]  \n" +
          "  }" }
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
