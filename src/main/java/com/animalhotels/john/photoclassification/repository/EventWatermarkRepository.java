package com.animalhotels.john.photoclassification.repository;

import com.animalhotels.john.photoclassification.entity.OfferScore;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EventWatermarkRepository extends CrudRepository<OfferScore, Long> {
    @Modifying
    @Query(value = """
        INSERT INTO john_event_watermark (aggregate_id, event_type, last_event_id)
        VALUES (:aggregateId, :eventType, :lastEventId)
        ON CONFLICT (aggregate_id, event_type)
        DO UPDATE SET last_event_id = :lastEventId
        WHERE john_event_watermark.last_event_id < :lastEventId""", nativeQuery = true)
    int upsertWatermark(@Param("aggregateId") Long aggregateId,
                        @Param("eventType") String eventType,
                        @Param("lastEventId") Long lastEventId);
}
