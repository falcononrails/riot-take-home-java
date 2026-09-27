package com.anaslimouri.riot.adapters.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.json.JsonMapper;

import com.anaslimouri.riot.application.RiotConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

class Base64CodecTest {
    private final JsonMapper mapper = new RiotConfiguration().jsonMapper();
    private final Base64Codec codec = new Base64Codec(mapper);

    @ParameterizedTest
    @ValueSource(strings = {"null", "false", "0", "\"\"", "\"30\"", "[]", "{}",
        """
        {"nested": [1, true, null]}
        """, "9007199254740993", "1.234567890123456789",
        "\"b64:MzA=\"", "\"\u00e9\ud83c\udfe0\""})
    void roundTripsEveryJsonType(String json) {
        Object original = mapper.readValue(json, Object.class);
        assertThat(codec.decodeOrOriginal(codec.encode(original))).isEqualTo(original);
    }

    @Test
    void usesTheDocumentedPrefix() {
        assertThat(codec.encode(30)).isEqualTo("b64:MzA=");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "MzA=", "1998-11-19", "b64:", "b64:???", "b64:MzA", "b64:MzB=", "b64:MzA==="})
    void leavesUnrecognizedAndMalformedStringsAlone(String input) {
        assertThat(codec.decodeOrOriginal(input)).isEqualTo(input);
    }

    @ParameterizedTest
    @ValueSource(strings = {"undefined", "{broken", "1 2", """
        {"x": 1, "x": 2}
        """})
    void doesNotDecodeInvalidOrAmbiguousJson(String json) {
        String input = "b64:" + Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        assertThat(codec.decodeOrOriginal(input)).isEqualTo(input);
    }

    @Test
    void rejectsInvalidUtf8() {
        String input = "b64:" + Base64.getEncoder().encodeToString(new byte[] {0x22, (byte) 0xff, 0x22});
        assertThat(codec.decodeOrOriginal(input)).isEqualTo(input);
    }
}
