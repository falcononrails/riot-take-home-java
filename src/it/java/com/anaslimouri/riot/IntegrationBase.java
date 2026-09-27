package com.anaslimouri.riot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import com.anaslimouri.riot.application.Application;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"riot.hmac-secret=test-secret", "server.address=127.0.0.1"})
abstract class IntegrationBase {
    @LocalServerPort
    private int port;

    @Autowired
    protected JsonMapper mapper;

    protected RequestSpecification givenApi() {
        return RestAssured.given().baseUri("http://127.0.0.1").port(port);
    }

    protected String resourceJson(String path) throws IOException {
        String directory = getClass().getSimpleName().toLowerCase(Locale.ROOT);
        return new ClassPathResource("com/anaslimouri/riot/" + directory + "/" + path)
            .getContentAsString(StandardCharsets.UTF_8);
    }

    protected Matcher<String> jsonMatcher(String expectedResource) throws IOException {
        JsonNode expected = mapper.readTree(resourceJson(expectedResource));
        return new FeatureMatcher<String, JsonNode>(equalTo(expected), "JSON matching " + expectedResource, "JSON") {
            @Override
            protected JsonNode featureValueOf(String actual) {
                return mapper.readTree(actual);
            }
        };
    }
}
