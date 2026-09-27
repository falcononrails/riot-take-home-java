package com.anaslimouri.riot.domain.usecases.requests;

import com.anaslimouri.riot.domain.models.Signature;
import com.anaslimouri.riot.domain.models.exceptions.InvalidSignatureException;

public record VerifySignatureRequest(Signature signature, Object data) {
    public VerifySignatureRequest {
        if (signature == null) {
            throw new InvalidSignatureException();
        }
    }
}
