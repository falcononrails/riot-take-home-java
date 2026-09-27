package com.anaslimouri.riot.domain.usecases;

import lombok.RequiredArgsConstructor;

import com.anaslimouri.riot.domain.ports.ValueCodec;
import com.anaslimouri.riot.domain.usecases.requests.DecryptPayloadRequest;
import com.anaslimouri.riot.domain.usecases.results.DecryptPayloadResult;
import com.anaslimouri.riot.domain.utils.JsonPayloadTransformer;

@RequiredArgsConstructor
public final class DecryptPayloadUseCase {
    private final ValueCodec codec;

    public DecryptPayloadResult execute(DecryptPayloadRequest request) {
        Object decrypted = JsonPayloadTransformer.mapDepthOne(request.payload(), value ->
            value instanceof String text ? codec.decodeOrOriginal(text) : value);
        return new DecryptPayloadResult(decrypted);
    }
}
