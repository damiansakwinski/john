package com.animalhotels.john.kafka.listener;

import com.animalhotels.john.photoclassification.ClassificationPersister;
import com.animalhotels.john.photoclassification.FaceClassifier;
import com.animalhotels.john.photoclassification.ImageDownloader;
import com.animalhotels.john.photoclassification.entity.OfferScore;
import com.animalhotels.john.photoclassification.repository.OfferScoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PhotoChangedListener.class, ClassificationPersister.class})
class PhotoChangedListenerTest {
    @Autowired
    private PhotoChangedListener sut;

    @Autowired
    private OfferScoreRepository offerScoreRepository;

    @MockitoBean
    private ImageDownloader imageDownloader;

    @MockitoBean
    private FaceClassifier faceClassifier;

    @Test
    void persistsTrueWhenFaceDetected() {
        byte[] image = new byte[]{1, 2, 3};
        Mockito.when(imageDownloader.download(URI.create("https://example.com/photo.jpg"))).thenReturn(image);
        Mockito.when(faceClassifier.containsFace(image)).thenReturn(true);

        this.sut.handle(new PhotoChangedEvent(8000L, "https://example.com/photo.jpg"), "1", "PhotoChanged");

        OfferScore saved = offerScoreRepository.findById(8000L).orElseThrow();
        Assertions.assertTrue(saved.getMainPhotoHasFace());
    }

    @Test
    void persistsFalseWhenNoFaceDetected() {
        byte[] image = new byte[]{1, 2, 3};
        Mockito.when(imageDownloader.download(URI.create("https://example.com/photo.jpg"))).thenReturn(image);
        Mockito.when(faceClassifier.containsFace(image)).thenReturn(false);

        this.sut.handle(new PhotoChangedEvent(8001L, "https://example.com/photo.jpg"), "1", "PhotoChanged");

        OfferScore saved = offerScoreRepository.findById(8001L).orElseThrow();
        Assertions.assertFalse(saved.getMainPhotoHasFace());
    }

    @Test
    void skipsOlderEvent() {
        byte[] image = new byte[]{1, 2, 3};
        Mockito.when(imageDownloader.download(Mockito.any())).thenReturn(image);
        Mockito.when(faceClassifier.containsFace(image)).thenReturn(true, false);

        this.sut.handle(new PhotoChangedEvent(8002L, "https://example.com/photo.jpg"), "5", "PhotoChanged");
        this.sut.handle(new PhotoChangedEvent(8002L, "https://example.com/photo2.jpg"), "3", "PhotoChanged");

        OfferScore saved = offerScoreRepository.findById(8002L).orElseThrow();
        Assertions.assertTrue(saved.getMainPhotoHasFace());
    }
}
