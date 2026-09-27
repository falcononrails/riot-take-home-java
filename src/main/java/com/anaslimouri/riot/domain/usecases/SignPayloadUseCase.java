package com.anaslimouri.riot.domain.usecases;

import lombok.RequiredArgsConstructor;

import com.anaslimouri.riot.domain.ports.PayloadSigner;
import com.anaslimouri.riot.domain.usecases.requests.SignPayloadRequest;
import com.anaslimouri.riot.domain.usecases.results.SignPayloadResult;

@RequiredArgsConstructor
public final class SignPayloadUseCase {
    private final PayloadSigner signer;

    public SignPayloadResult execute(SignPayloadRequest request) {
        return new SignPayloadResult(signer.sign(request.payload()));
    }
}
