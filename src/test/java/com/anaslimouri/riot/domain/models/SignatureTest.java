package com.anaslimouri.riot.domain.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.anaslimouri.riot.domain.models.exceptions.InvalidSignatureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignatureTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingOrBlankValues(String value) {
        assertThatThrownBy(() -> new Signature(value))
            .isInstanceOf(InvalidSignatureException.class);
    }

    @Test
    void preservesTheValueWithoutImposingAnAlgorithmFormat() {
        var signature = new Signature("test-signature:payload");

        assertThat(signature.value()).isEqualTo("test-signature:payload");
        assertThat(signature).isEqualTo(new Signature("test-signature:payload"));
    }
}
