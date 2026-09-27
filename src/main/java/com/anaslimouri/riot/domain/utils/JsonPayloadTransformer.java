package com.anaslimouri.riot.domain.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonPayloadTransformer {
    // Payloads are JSON values: maps with string keys, lists, scalars or null.
    public Object mapDepthOne(Object payload, UnaryOperator<Object> transform) {
        if (payload instanceof Map<?, ?> map) {
            var result = new LinkedHashMap<String, Object>();
            map.forEach((key, value) -> {
                if (!(key instanceof String name)) {
                    throw new IllegalArgumentException("JSON object keys must be strings");
                }
                result.put(name, transform.apply(value));
            });
            return result;
        }
        if (payload instanceof List<?> list) {
            return list.stream().map(transform).toList();
        }
        return payload;
    }
}
