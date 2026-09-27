package com.anaslimouri.riot.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.json.JsonMapper;

import com.anaslimouri.riot.adapters.crypto.Base64Codec;
import com.anaslimouri.riot.adapters.crypto.HmacSha256Signer;
import com.anaslimouri.riot.domain.ports.PayloadSigner;
import com.anaslimouri.riot.domain.ports.ValueCodec;
import com.anaslimouri.riot.domain.usecases.DecryptPayloadUseCase;
import com.anaslimouri.riot.domain.usecases.EncryptPayloadUseCase;
import com.anaslimouri.riot.domain.usecases.SignPayloadUseCase;
import com.anaslimouri.riot.domain.usecases.VerifySignatureUseCase;

@Configuration(proxyBeanMethods = false)
public class RiotConfiguration {
    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
            .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .withCoercionConfig(String.class, coercion -> coercion
                .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
                .setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail))
            .build();
    }

    @Bean
    ValueCodec valueCodec(JsonMapper mapper) {
        return new Base64Codec(mapper);
    }

    @Bean
    PayloadSigner payloadSigner(JsonMapper mapper, @Value("${riot.hmac-secret}") String secret) {
        return new HmacSha256Signer(mapper, secret);
    }

    @Bean
    EncryptPayloadUseCase encryptPayloadUseCase(ValueCodec codec) {
        return new EncryptPayloadUseCase(codec);
    }

    @Bean
    DecryptPayloadUseCase decryptPayloadUseCase(ValueCodec codec) {
        return new DecryptPayloadUseCase(codec);
    }

    @Bean
    SignPayloadUseCase signPayloadUseCase(PayloadSigner signer) {
        return new SignPayloadUseCase(signer);
    }

    @Bean
    VerifySignatureUseCase verifySignatureUseCase(PayloadSigner signer) {
        return new VerifySignatureUseCase(signer);
    }
}
