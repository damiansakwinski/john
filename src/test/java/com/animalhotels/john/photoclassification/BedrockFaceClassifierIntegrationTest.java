package com.animalhotels.john.photoclassification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class BedrockFaceClassifierIntegrationTest {
    @Autowired
    private BedrockFaceClassifier sut;

    @Test
    void returnsFalseForAnimalOnly() throws IOException {
        byte[] img = getClass().getResourceAsStream("/faceclassifiertest/cat.jpeg").readAllBytes();

        Assertions.assertFalse(this.sut.containsFace(img));
    }

    @Test
    void returnsTrueForPersonWithAnimal() throws IOException {
        byte[] img = getClass().getResourceAsStream("/faceclassifiertest/dog_and_face.jpeg").readAllBytes();

        Assertions.assertTrue(this.sut.containsFace(img));
    }

    @Test
    void returnsFalseForBlurredFace() throws IOException {
        byte[] img = getClass().getResourceAsStream("/faceclassifiertest/face_blurred.jpeg").readAllBytes();

        Assertions.assertFalse(this.sut.containsFace(img));
    }

    @Test
    void returnsTrueForPerson() throws IOException {
        byte[] img = getClass().getResourceAsStream("/faceclassifiertest/person.jpeg").readAllBytes();

        Assertions.assertTrue(this.sut.containsFace(img));
    }
}
