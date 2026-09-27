package com.anaslimouri.riot.domain.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.anaslimouri.riot.domain.ports.ValueCodec;
import com.anaslimouri.riot.domain.usecases.requests.DecryptPayloadRequest;
import com.anaslimouri.riot.domain.usecases.requests.EncryptPayloadRequest;
import com.anaslimouri.riot.domain.usecases.results.DecryptPayloadResult;
import com.anaslimouri.riot.domain.usecases.results.EncryptPayloadResult;

import static org.assertj.core.api.Assertions.assertThat;

class PayloadUseCasesTest {
    @Test
    void encryptsWholeDepthOneValuesWithoutChangingTheInput() {
        var encodedValues = new ArrayList<>();
        var useCase = new EncryptPayloadUseCase(new ValueCodec() {
            public String encode(Object value) {
                encodedValues.add(value);
                return "encoded-" + encodedValues.size();
            }

            public Object decodeOrOriginal(String value) {
                throw new UnsupportedOperationException();
            }
        });
        var nested = Map.of("email", "ana@example.com");
        var original = new LinkedHashMap<String, Object>();
        original.put("name", "Ana");
        original.put("contact", nested);
        original.put("scores", List.of(1, 2));
        var snapshot = new LinkedHashMap<>(original);

        assertThat(useCase.execute(new EncryptPayloadRequest(original))).isEqualTo(new EncryptPayloadResult(Map.of(
            "name", "encoded-1", "contact", "encoded-2", "scores", "encoded-3")));
        assertThat(encodedValues).containsExactly("Ana", nested, List.of(1, 2));
        assertThat(original).isEqualTo(snapshot);
    }

    @Test
    void decryptPreservesNullFalseAndUnrecognizedValues() {
        var useCase = new DecryptPayloadUseCase(new ValueCodec() {
            public String encode(Object value) {
                throw new UnsupportedOperationException();
            }

            public Object decodeOrOriginal(String value) {
                return switch (value) {
                    case "encoded-null" -> null;
                    case "encoded-false" -> false;
                    default -> value;
                };
            }
        });
        var nested = Map.of("value", "encoded-null");
        var request = new DecryptPayloadRequest(Arrays.asList("encoded-null", "encoded-false", "plain", 0, nested));
        assertThat(useCase.execute(request))
            .isEqualTo(new DecryptPayloadResult(Arrays.asList(null, false, "plain", 0, nested)));
    }

    @Test
    void scalarRootsAndEmptyContainersHaveNothingToTransform() {
        var codec = new ValueCodec() {
            public String encode(Object value) {
                throw new AssertionError("A scalar root has no depth-one values");
            }

            public Object decodeOrOriginal(String value) {
                throw new AssertionError("A scalar root has no depth-one values");
            }
        };
        var encrypt = new EncryptPayloadUseCase(codec);
        var decrypt = new DecryptPayloadUseCase(codec);
        for (Object value : Arrays.asList(null, false, 0, "hello", List.of(), Map.of())) {
            assertThat(encrypt.execute(new EncryptPayloadRequest(value))).isEqualTo(new EncryptPayloadResult(value));
            assertThat(decrypt.execute(new DecryptPayloadRequest(value))).isEqualTo(new DecryptPayloadResult(value));
        }
    }
}
