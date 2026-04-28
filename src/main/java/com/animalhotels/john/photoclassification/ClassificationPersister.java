package com.animalhotels.john.photoclassification;

import com.animalhotels.john.photoclassification.entity.OfferScore;
import com.animalhotels.john.photoclassification.repository.EventWatermarkRepository;
import com.animalhotels.john.photoclassification.repository.OfferScoreRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;

@Component
public class ClassificationPersister {
    private final EventWatermarkRepository eventWatermarkRepository;
    private final OfferScoreRepository offerScoreRepository;

    public ClassificationPersister(EventWatermarkRepository eventWatermarkRepository,
                                   OfferScoreRepository offerScoreRepository) {
        this.eventWatermarkRepository = eventWatermarkRepository;
        this.offerScoreRepository = offerScoreRepository;
    }

    @Transactional
    public void saveIfLatestEvent(OfferScore offerScore, Long eventId, String eventType) {
        boolean newerEventExists = 0 == this.eventWatermarkRepository.upsertWatermark(offerScore.getOfferId(), eventType, eventId);

        if (newerEventExists) {
            return;
        }

        this.offerScoreRepository.save(offerScore);
    }
}
