package com.anaslimouri.riot.adapters.crypto;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import com.anaslimouri.riot.domain.models.Signature;
import com.anaslimouri.riot.domain.ports.PayloadSigner;

public final class HmacSha256Signer implements PayloadSigner {
    private static final String ALGORITHM = "HmacSHA256";
    private final ObjectWriter writer;
    private final SecretKeySpec key;

    public HmacSha256Signer(JsonMapper mapper, String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("HMAC_SECRET must not be blank");
        }
        this.writer = mapper.writer().with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    @Override
    public Signature sign(Object payload) {
        return new Signature(HexFormat.of().formatHex(digest(payload)));
    }

    @Override
    public boolean verify(Object payload, Signature signature) {
        if (signature == null || !signature.value().matches("[a-f0-9]{64}")) {
            return false;
        }
        return MessageDigest.isEqual(digest(payload), HexFormat.of().parseHex(signature.value()));
    }

    private byte[] digest(Object payload) {
        try {
            // Mac is mutable, so requests must not share an instance.
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            return mac.doFinal(writer.writeValueAsBytes(payload));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("HMAC-SHA256 is unavailable", exception);
        }
    }
}
