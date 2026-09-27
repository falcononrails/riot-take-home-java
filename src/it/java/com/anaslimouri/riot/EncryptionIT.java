package com.anaslimouri.riot;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.http.ContentType.JSON;

class EncryptionIT extends IntegrationBase {
    @Test
    void encryptsTheExampleAtDepthOneAndRestoresItsTypes() throws IOException {
        String encrypted = givenApi().contentType(JSON)
            .body(resourceJson("input/payload.json"))
            .post("/encrypt")
            .then().statusCode(200).contentType(JSON)
            .body(jsonMatcher("output/encrypted-payload.json"))
            .extract().asString();

        givenApi().contentType(JSON).body(encrypted)
            .post("/decrypt")
            .then().statusCode(200).contentType(JSON)
            .body(jsonMatcher("input/payload.json"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"empty-array", "empty-object", "mixed-array", "mixed-types", "precise-numbers"})
    void encryptDecryptRoundTrip(String fixture) throws IOException {
        String path = "input/" + fixture + ".json";
        String encrypted = givenApi().contentType(JSON).body(resourceJson(path))
            .post("/encrypt")
            .then().statusCode(200).contentType(JSON)
            .extract().asString();

        givenApi().contentType(JSON).body(encrypted)
            .post("/decrypt")
            .then().statusCode(200).contentType(JSON)
            .body(jsonMatcher(path));
    }

    @ParameterizedTest
    @ValueSource(strings = {"root-null", "root-boolean", "root-number", "root-string"})
    void scalarRootsRemainJsonValues(String fixture) throws IOException {
        String path = "input/" + fixture + ".json";
        for (String endpoint : List.of("/encrypt", "/decrypt")) {
            givenApi().contentType(JSON).body(resourceJson(path))
                .post(endpoint)
                .then().statusCode(200).contentType(JSON)
                .body(jsonMatcher(path));
        }
    }

    @Test
    void decryptKeepsPlaintextAndMalformedEncodedValues() throws IOException {
        givenApi().contentType(JSON)
            .body(resourceJson("input/mixed-encoded-values.json"))
            .post("/decrypt")
            .then().statusCode(200).contentType(JSON)
            .body(jsonMatcher("output/mixed-decoded-values.json"));
    }
}
