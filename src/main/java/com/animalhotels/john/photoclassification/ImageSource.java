package com.animalhotels.john.photoclassification;

import com.fasterxml.jackson.annotation.JsonProperty;

record ImageSource(String type, @JsonProperty("media_type") String mediaType, String data) {}

