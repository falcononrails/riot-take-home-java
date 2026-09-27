package com.anaslimouri.riot.domain.usecases;

import lombok.RequiredArgsConstructor;

import com.anaslimouri.riot.domain.ports.ValueCodec;
import com.anaslimouri.riot.domain.usecases.requests.EncryptPayloadRequest;
import com.anaslimouri.riot.domain.usecases.results.EncryptPayloadResult;
import com.anaslimouri.riot.domain.utils.JsonPayloadTransformer;

@RequiredArgsConstructor
public final class EncryptPayloadUseCase {
    private final ValueCodec codec;

    public EncryptPayloadResult execute(EncryptPayloadRequest request) {
        Object encrypted = JsonPayloadTransformer.mapDepthOne(request.payload(), codec::encode);
        return new EncryptPayloadResult(encrypted);
    }
}
