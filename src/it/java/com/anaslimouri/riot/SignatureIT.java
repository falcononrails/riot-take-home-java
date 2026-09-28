package com.anaslimouri.riot;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;

class SignatureIT extends IntegrationBase {
    @ParameterizedTest
    @ValueSource(strings = {"root-null", "root-boolean", "root-number", "root-string", "empty-array",
        "nested-array"})
    void signVerifyAcceptsEveryJsonRoot(String fixture) throws IOException {
        String payload = resourceJson("input/" + fixture + ".json");
        String signature = givenApi().contentType(JSON).body(payload)
            .post("/sign")
            .then().statusCode(200).contentType(JSON)
            .extract().path("signature");

        givenApi().contentType(JSON).body(envelope(signature, payload))
            .post("/verify")
            .then().statusCode(204).body(emptyString());
    }

    @Test
    void signsByValueAndRejectsTampering() throws IOException {
        String signature = givenApi().contentType(JSON)
            .body(resourceJson("input/sign-original.json"))
            .post("/sign")
            .then().statusCode(200).contentType(JSON)
            .extract().path("signature");

        String reordered = resourceJson("input/sign-reordered.json");
        givenApi().contentType(JSON).body(reordered)
            .post("/sign")
            .then().statusCode(200).contentType(JSON)
            .body("signature", equalTo(signature));

        givenApi().contentType(JSON).body(envelope(signature, reordered))
            .post("/verify")
            .then().statusCode(204).body(emptyString());

        givenApi().contentType(JSON)
            .body(envelope(signature, resourceJson("input/sign-tampered.json")))
            .post("/verify")
            .then().statusCode(400).contentType(JSON)
            .body(jsonMatcher("output/invalid-signature.json"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"root-null", "root-boolean", "root-number", "root-string", "empty-array",
        "empty-object", "verify-numeric-signature", "verify-decimal-signature", "verify-boolean-signature",
        "verify-object-signature", "verify-array-signature", "verify-missing-data", "verify-missing-signature"})
    void verifyRejectsMalformedEnvelopes(String fixture) throws IOException {
        givenApi().contentType(JSON)
            .body(resourceJson("input/" + fixture + ".json"))
            .post("/verify")
            .then().statusCode(400).contentType(JSON)
            .body(jsonMatcher("output/invalid-json.json"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"verify-invalid-signature", "verify-null-signature", "verify-empty-signature",
        "verify-blank-signature"})
    void verifyMapsInvalidSignaturesToBadRequest(String fixture) throws IOException {
        givenApi().contentType(JSON)
            .body(resourceJson("input/" + fixture + ".json"))
            .post("/verify")
            .then().statusCode(400).contentType(JSON)
            .body(jsonMatcher("output/invalid-signature.json"));
    }

    @Test
    void verifyAcceptsExplicitNullDataAndIgnoresAdditionalEnvelopeFields() throws IOException {
        givenApi().contentType(JSON)
            .body(resourceJson("input/verify-null-data.json"))
            .post("/verify")
            .then().statusCode(204).body(emptyString());
    }

    private String envelope(String signature, String payload) {
        var envelope = mapper.createObjectNode().put("signature", signature);
        envelope.set("data", mapper.readTree(payload));
        return mapper.writeValueAsString(envelope);
    }
}
