package com.animalhotels.john.kafka.listener;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PhotoChangedEvent(Long offerId, String mediaObjectUrl) {
}
