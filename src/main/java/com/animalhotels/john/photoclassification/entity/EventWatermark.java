package com.animalhotels.john.photoclassification.entity;

import jakarta.persistence.*;

@Entity
public class EventWatermark {
    @Id
    private Long aggregateId;

    @Column
    private String eventType;

    @Column
    private Long lastEventId;

    public Long getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(Long aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getLastEventId() {
        return lastEventId;
    }

    public void setLastEventId(Long lastEventId) {
        this.lastEventId = lastEventId;
    }
}
