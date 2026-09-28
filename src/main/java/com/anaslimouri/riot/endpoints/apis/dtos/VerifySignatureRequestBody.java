package com.anaslimouri.riot.endpoints.apis.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VerifySignatureRequestBody(
    @JsonProperty(required = true) String signature,
    @JsonProperty(required = true) JsonNode data
) {}
