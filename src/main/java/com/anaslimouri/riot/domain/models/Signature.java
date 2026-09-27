package com.anaslimouri.riot.domain.models;

import com.anaslimouri.riot.domain.models.exceptions.InvalidSignatureException;

public record Signature(String value) {
    public Signature {
        if (value == null || value.isBlank()) {
            throw new InvalidSignatureException();
        }
    }
}
