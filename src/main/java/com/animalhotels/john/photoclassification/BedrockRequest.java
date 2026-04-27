package com.animalhotels.john.photoclassification;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

record BedrockRequest(
    @JsonProperty("anthropic_version") String anthropicVersion,
    @JsonProperty("max_tokens") int maxTokens,
    int temperature,
    List<Message> messages
) {
}

