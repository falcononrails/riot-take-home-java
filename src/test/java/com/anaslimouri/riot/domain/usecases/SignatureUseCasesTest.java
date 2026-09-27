package com.anaslimouri.riot.domain.usecases;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.anaslimouri.riot.domain.models.Signature;
import com.anaslimouri.riot.domain.models.exceptions.InvalidSignatureException;
import com.anaslimouri.riot.domain.ports.PayloadSigner;
import com.anaslimouri.riot.domain.usecases.requests.SignPayloadRequest;
import com.anaslimouri.riot.domain.usecases.requests.VerifySignatureRequest;
import com.anaslimouri.riot.domain.usecases.results.SignPayloadResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SignatureUseCasesTest {
    private final PayloadSigner signer = mock(PayloadSigner.class);
    private final Signature signature = new Signature("signature");

    @Test
    void signsTheRequestedPayload() {
        var payload = Map.of("value", 42);
        when(signer.sign(payload)).thenReturn(signature);

        assertThat(new SignPayloadUseCase(signer).execute(new SignPayloadRequest(payload)))
            .isEqualTo(new SignPayloadResult(signature));
        verify(signer).sign(payload);
    }

    @Test
    void acceptsAVerifiedNullPayload() {
        when(signer.verify(null, signature)).thenReturn(true);

        assertThatCode(() -> new VerifySignatureUseCase(signer)
            .execute(new VerifySignatureRequest(signature, null)))
            .doesNotThrowAnyException();
        verify(signer).verify(null, signature);
    }

    @Test
    void rejectsAFailedVerificationWithADomainException() {
        var payload = Map.of("value", 42);
        when(signer.verify(payload, signature)).thenReturn(false);

        assertThatThrownBy(() -> new VerifySignatureUseCase(signer)
            .execute(new VerifySignatureRequest(signature, payload)))
            .isInstanceOf(InvalidSignatureException.class)
            .hasMessage("Invalid signature or payload");
        verify(signer).verify(payload, signature);
    }

    @Test
    void verificationRequiresASignature() {
        assertThatThrownBy(() -> new VerifySignatureRequest(null, null))
            .isInstanceOf(InvalidSignatureException.class);
    }
}
