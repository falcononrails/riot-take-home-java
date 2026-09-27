package com.anaslimouri.riot.domain.ports;

import com.anaslimouri.riot.domain.models.Signature;

public interface PayloadSigner {
    Signature sign(Object payload);

    boolean verify(Object payload, Signature signature);
}
