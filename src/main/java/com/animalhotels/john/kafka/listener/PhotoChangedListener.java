package com.animalhotels.john.kafka.listener;

import com.animalhotels.john.photoclassification.*;
import com.animalhotels.john.photoclassification.entity.OfferScore;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class PhotoChangedListener {
    private static final Logger log = LoggerFactory.getLogger(PhotoChangedListener.class);
    private final ClassificationPersister classificationPersister;
    private final ImageDownloader imageDownloader;
    private final FaceClassifier faceClassifier;

    public PhotoChangedListener(ClassificationPersister classificationPersister,
                                ImageDownloader imageDownloader,
                                FaceClassifier faceClassifier) {
        this.classificationPersister = classificationPersister;
        this.imageDownloader = imageDownloader;
        this.faceClassifier = faceClassifier;
    }

    @RetryableTopic(
        backOff = @BackOff(delay = 60000, multiplier = 2)
    )
    @KafkaListener(topics = "outbox.OfferPhoto", groupId = "photo-classifier")
    public void handle(PhotoChangedEvent photoChangedEvent,
                       @Header("id") String eventId,
                       @Header("event_type") String eventType) {
        byte[] image = this.imageDownloader.download(URI.create(photoChangedEvent.mediaObjectUrl()));
        OfferScore offerScore = OfferScore.create(photoChangedEvent.offerId(), this.faceClassifier.containsFace(image));
        this.classificationPersister.saveIfNewer(offerScore, Long.parseLong(eventId), eventType);
    }

    @DltHandler
    public void handleDlt(PhotoChangedEvent photoChangedEvent,
                          @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stacktrace) {
        log.error("DLT: failed to classify offer {}, url: {}\n{}", photoChangedEvent.offerId(), photoChangedEvent.mediaObjectUrl(), stacktrace);
    }
}
