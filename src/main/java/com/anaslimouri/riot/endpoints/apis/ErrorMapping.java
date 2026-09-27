package com.anaslimouri.riot.endpoints.apis;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.anaslimouri.riot.domain.models.exceptions.InvalidSignatureException;
import com.anaslimouri.riot.endpoints.apis.dtos.ErrorResponseBody;

@RestControllerAdvice
public class ErrorMapping {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody invalidJson() {
        return new ErrorResponseBody("Expected a valid JSON payload");
    }

    @ExceptionHandler(InvalidSignatureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody invalidSignature(InvalidSignatureException exception) {
        return new ErrorResponseBody(exception.getMessage());
    }
}
