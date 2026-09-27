package com.anaslimouri.riot.adapters.crypto;

import java.util.Base64;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import com.anaslimouri.riot.domain.ports.ValueCodec;

@RequiredArgsConstructor
public final class Base64Codec implements ValueCodec {
    private static final String PREFIX = "b64:";
    private final JsonMapper mapper;

    @Override
    public String encode(Object value) {
        return PREFIX + Base64.getEncoder().encodeToString(mapper.writeValueAsBytes(value));
    }

    @Override
    public Object decodeOrOriginal(String value) {
        if (!value.startsWith(PREFIX)) {
            return value;
        }
        String encoded = value.substring(PREFIX.length());
        try {
            byte[] decoded = Base64.getDecoder().decode(encoded);
            if (encoded.isEmpty() || !Base64.getEncoder().encodeToString(decoded).equals(encoded)) {
                return value;
            }
            return mapper.readValue(decoded, Object.class);
        } catch (IllegalArgumentException | JacksonException exception) {
            return value;
        }
    }
}
