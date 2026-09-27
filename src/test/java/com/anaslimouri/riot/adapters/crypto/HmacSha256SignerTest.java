package com.anaslimouri.riot.adapters.crypto;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import com.anaslimouri.riot.application.RiotConfiguration;
import com.anaslimouri.riot.domain.models.Signature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HmacSha256SignerTest {
    private final JsonMapper mapper = new RiotConfiguration().jsonMapper();
    private final HmacSha256Signer signer = new HmacSha256Signer(mapper, "test-secret");

    @Test
    void signsSortedJsonWithHmacSha256() throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec("test-secret".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String sortedJson = """
            {"a":1,"b":2}
            """.strip();
        var expected = new Signature(HexFormat.of().formatHex(mac.doFinal(sortedJson.getBytes(StandardCharsets.UTF_8))));

        assertThat(signer.sign(Map.of("b", 2, "a", 1))).isEqualTo(expected);
        assertThat(signer.verify(Map.of("a", 1, "b", 2), expected)).isTrue();
    }

    @Test
    void sortsNestedObjectKeysButPreservesArrayOrder() {
        Object first = mapper.readValue("""
            {
              "b": 1,
              "a": [{"z": 2, "x": 3}, 4]
            }
            """, Object.class);
        Object reordered = mapper.readValue("""
            {
              "a": [{"x": 3, "z": 2}, 4],
              "b": 1
            }
            """, Object.class);
        assertThat(signer.sign(first)).isEqualTo(signer.sign(reordered));
        assertThat(signer.sign(List.of(1, 2))).isNotEqualTo(signer.sign(List.of(2, 1)));
    }

    @Test
    void preservesLargeIntegers() {
        assertThat(signer.sign(mapper.readValue("9007199254740993", Object.class)))
            .isNotEqualTo(signer.sign(mapper.readValue("9007199254740992", Object.class)));
    }

    @Test
    void sortingForSigningDoesNotChangePayloadOrSharedSerialization() {
        Object payload = mapper.readValue("""
            {
              "z": [1.0, 1e3, -0.0],
              "a": {"n": 1.200}
            }
            """, Object.class);
        Object equivalent = mapper.readValue("""
            {
              "a": {"n": 1.200},
              "z": [1.0, 1e3, -0.0]
            }
            """, Object.class);
        String originalJson = mapper.writeValueAsString(payload);

        assertThat(signer.sign(payload)).isEqualTo(signer.sign(equivalent));
        assertThat(mapper.writeValueAsString(payload)).isEqualTo(originalJson);
    }

    @Test
    void rejectsTamperingWrongKeysAndMalformedSignatures() {
        Signature signature = signer.sign(Map.of("value", 42));
        assertThat(signer.verify(Map.of("value", 43), signature)).isFalse();
        assertThat(signer.verify(Map.of("value", "42"), signature)).isFalse();
        assertThat(new HmacSha256Signer(mapper, "other-secret").verify(Map.of("value", 42), signature)).isFalse();
        assertThat(signer.verify(null, null)).isFalse();
        for (String invalid : List.of("bad", "0".repeat(64), "g".repeat(64), signature.value().substring(1))) {
            assertThat(signer.verify(Map.of("value", 42), new Signature(invalid))).isFalse();
        }
    }

    @Test
    void concurrentRequestsDoNotShareMutableMacState() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var results = IntStream.range(0, 50)
                .mapToObj(index -> executor.submit(() -> signer.sign(Map.of("value", index))))
                .toList();
            for (int index = 0; index < results.size(); index++) {
                assertThat(results.get(index).get()).isEqualTo(signer.sign(Map.of("value", index)));
            }
        }
    }

    @Test
    void requiresANonBlankSecret() {
        assertThatThrownBy(() -> new HmacSha256Signer(mapper, " "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("HMAC_SECRET must not be blank");
    }
}
