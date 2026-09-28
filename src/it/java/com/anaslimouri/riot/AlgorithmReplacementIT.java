package com.anaslimouri.riot;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.json.JsonMapper;

import com.anaslimouri.riot.domain.models.Signature;
import com.anaslimouri.riot.domain.ports.PayloadSigner;
import com.anaslimouri.riot.domain.ports.ValueCodec;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.emptyString;

@Import(AlgorithmReplacementIT.ReplacementAlgorithms.class)
class AlgorithmReplacementIT extends IntegrationBase {
    @Test
    void allRoutesUseTheInjectedAlgorithmsWithoutControllerChanges() throws IOException {
        givenApi().contentType(JSON)
            .body(resourceJson("input/encrypt.json")).post("/encrypt")
            .then().statusCode(200).contentType(JSON)
            .body(jsonMatcher("output/encrypt.json"));

        givenApi().contentType(JSON)
            .body(resourceJson("input/decrypt.json")).post("/decrypt")
            .then().statusCode(200).contentType(JSON)
            .body(jsonMatcher("output/decrypt.json"));

        givenApi().contentType(JSON)
            .body(resourceJson("input/sign.json")).post("/sign")
            .then().statusCode(200).contentType(JSON)
            .body(jsonMatcher("output/sign.json"));

        givenApi().contentType(JSON)
            .body(resourceJson("input/verify-valid.json")).post("/verify")
            .then().statusCode(204).body(emptyString());

        givenApi().contentType(JSON)
            .body(resourceJson("input/verify-invalid.json")).post("/verify")
            .then().statusCode(400).contentType(JSON)
            .body(jsonMatcher("output/invalid-signature.json"));
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class ReplacementAlgorithms {
        @Bean
        @Primary
        ValueCodec testCodec(JsonMapper mapper) {
            return new ValueCodec() {
                public String encode(Object value) {
                    return "test:" + mapper.writeValueAsString(value);
                }

                public Object decodeOrOriginal(String value) {
                    return value.startsWith("test:") ? mapper.readValue(value.substring(5), Object.class) : value;
                }
            };
        }

        @Bean
        @Primary
        PayloadSigner testSigner(JsonMapper mapper) {
            // Deliberately not cryptographic. This checks dependency wiring.
            return new PayloadSigner() {
                public Signature sign(Object payload) {
                    return new Signature("test-signature:" + mapper.writeValueAsString(payload));
                }

                public boolean verify(Object payload, Signature signature) {
                    return sign(payload).equals(signature);
                }
            };
        }
    }
}
