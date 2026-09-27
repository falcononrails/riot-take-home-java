package com.anaslimouri.riot.domain.usecases;

import lombok.RequiredArgsConstructor;

import com.anaslimouri.riot.domain.models.exceptions.InvalidSignatureException;
import com.anaslimouri.riot.domain.ports.PayloadSigner;
import com.anaslimouri.riot.domain.usecases.requests.VerifySignatureRequest;

@RequiredArgsConstructor
public final class VerifySignatureUseCase {
    private final PayloadSigner signer;

    public void execute(VerifySignatureRequest request) {
        if (!signer.verify(request.data(), request.signature())) {
            throw new InvalidSignatureException();
        }
    }
}
