package com.anaslimouri.riot.domain.models.exceptions;

public final class InvalidSignatureException extends RuntimeException {
    public InvalidSignatureException() {
        super("Invalid signature or payload");
    }
}
