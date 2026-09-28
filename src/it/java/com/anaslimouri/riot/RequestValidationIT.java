package com.anaslimouri.riot;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.TEXT;

class RequestValidationIT extends IntegrationBase {
    private static final List<String> ENDPOINTS = List.of("/encrypt", "/decrypt", "/sign", "/verify");

    @Test
    void allRoutesRejectMalformedMissingAndAmbiguousJson() throws IOException {
        for (String endpoint : ENDPOINTS) {
            for (String fixture : List.of("empty-body", "malformed", "trailing-value", "duplicate-keys")) {
                givenApi().contentType(JSON)
                    .body(resourceJson("input/" + fixture + ".json"))
                    .post(endpoint)
                    .then().statusCode(400).contentType(JSON)
                    .body(jsonMatcher("output/invalid-json.json"));
            }
        }
    }

    @Test
    void allRoutesRejectUnsupportedContentTypes() throws IOException {
        for (String endpoint : ENDPOINTS) {
            givenApi().contentType(TEXT).body(resourceJson("input/empty-object.json"))
                .post(endpoint)
                .then().statusCode(415);
        }
    }
}
