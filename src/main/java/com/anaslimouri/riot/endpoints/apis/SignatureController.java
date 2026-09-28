package com.anaslimouri.riot.endpoints.apis;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import com.anaslimouri.riot.domain.models.Signature;
import com.anaslimouri.riot.domain.usecases.SignPayloadUseCase;
import com.anaslimouri.riot.domain.usecases.VerifySignatureUseCase;
import com.anaslimouri.riot.domain.usecases.requests.SignPayloadRequest;
import com.anaslimouri.riot.domain.usecases.requests.VerifySignatureRequest;
import com.anaslimouri.riot.endpoints.apis.dtos.SignatureResponseBody;
import com.anaslimouri.riot.endpoints.apis.dtos.VerifySignatureRequestBody;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SignatureController {
    private final SignPayloadUseCase signPayloadUseCase;
    private final VerifySignatureUseCase verifySignatureUseCase;
    private final JsonMapper mapper;

    @PostMapping("/sign")
    public SignatureResponseBody sign(@RequestBody JsonNode payload) {
        var request = new SignPayloadRequest(mapper.convertValue(payload, Object.class));
        var result = signPayloadUseCase.execute(request);
        return new SignatureResponseBody(result.signature().value());
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verify(@RequestBody VerifySignatureRequestBody body) {
        var request = new VerifySignatureRequest(new Signature(body.signature()), mapper.convertValue(body.data(), Object.class));
        verifySignatureUseCase.execute(request);
    }
}
