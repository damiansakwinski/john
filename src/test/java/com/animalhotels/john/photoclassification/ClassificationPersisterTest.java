package com.animalhotels.john.photoclassification;

import com.animalhotels.john.photoclassification.entity.OfferScore;
import com.animalhotels.john.photoclassification.repository.OfferScoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ClassificationPersister.class)
class ClassificationPersisterTest {
    @Autowired
    private ClassificationPersister sut;

    @Autowired
    private OfferScoreRepository offerScoreRepository;


    @Test
    void savesOfferScoreForNewEvent() {
        sut.saveIfLatestEvent(OfferScore.create(9000L, true), 1L, "PhotoChanged");

        OfferScore saved = offerScoreRepository.findById(9000L).orElseThrow();
        Assertions.assertTrue(saved.getMainPhotoHasFace());
    }

    @Test
    void updatesOfferScoreWhenNewerEventArrives() {
        sut.saveIfLatestEvent(OfferScore.create(9001L, true), 1L, "PhotoChanged");
        sut.saveIfLatestEvent(OfferScore.create(9001L, false), 2L, "PhotoChanged");

        OfferScore saved = offerScoreRepository.findById(9001L).orElseThrow();
        Assertions.assertFalse(saved.getMainPhotoHasFace());
    }

    @Test
    void skipsOfferScoreWhenOlderEventArrives() {
        sut.saveIfLatestEvent(OfferScore.create(9002L, true), 5L, "PhotoChanged");
        sut.saveIfLatestEvent(OfferScore.create(9002L, false), 3L, "PhotoChanged");

        OfferScore saved = offerScoreRepository.findById(9002L).orElseThrow();
        Assertions.assertTrue(saved.getMainPhotoHasFace());
    }

    @Test
    void skipsOfferScoreWhenDuplicateEventArrives() {
        sut.saveIfLatestEvent(OfferScore.create(9003L, true), 1L, "PhotoChanged");
        sut.saveIfLatestEvent(OfferScore.create(9003L, false), 1L, "PhotoChanged");

        OfferScore saved = offerScoreRepository.findById(9003L).orElseThrow();
        Assertions.assertTrue(saved.getMainPhotoHasFace());
    }
}