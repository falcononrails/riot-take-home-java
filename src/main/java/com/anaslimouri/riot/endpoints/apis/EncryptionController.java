package com.anaslimouri.riot.endpoints.apis;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import com.anaslimouri.riot.domain.usecases.DecryptPayloadUseCase;
import com.anaslimouri.riot.domain.usecases.EncryptPayloadUseCase;
import com.anaslimouri.riot.domain.usecases.requests.DecryptPayloadRequest;
import com.anaslimouri.riot.domain.usecases.requests.EncryptPayloadRequest;
import com.anaslimouri.riot.endpoints.apis.dtos.DecryptPayloadResponseBody;
import com.anaslimouri.riot.endpoints.apis.dtos.EncryptPayloadResponseBody;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EncryptionController {
    private final EncryptPayloadUseCase encryptPayloadUseCase;
    private final DecryptPayloadUseCase decryptPayloadUseCase;
    private final JsonMapper mapper;

    @PostMapping("/encrypt")
    public EncryptPayloadResponseBody encrypt(@RequestBody JsonNode payload) {
        var request = new EncryptPayloadRequest(mapper.convertValue(payload, Object.class));
        var result = encryptPayloadUseCase.execute(request);
        return new EncryptPayloadResponseBody(result.payload());
    }

    @PostMapping("/decrypt")
    public DecryptPayloadResponseBody decrypt(@RequestBody JsonNode payload) {
        var request = new DecryptPayloadRequest(mapper.convertValue(payload, Object.class));
        var result = decryptPayloadUseCase.execute(request);
        return new DecryptPayloadResponseBody(result.payload());
    }
}
