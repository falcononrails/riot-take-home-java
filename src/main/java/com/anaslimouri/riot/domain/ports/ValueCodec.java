package com.anaslimouri.riot.domain.ports;

public interface ValueCodec {
    String encode(Object value);

    // Return the original string when it is not recognized or cannot be decoded.
    Object decodeOrOriginal(String value);
}
