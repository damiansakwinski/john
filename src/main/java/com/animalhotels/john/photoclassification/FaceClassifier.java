package com.animalhotels.john.photoclassification;

public interface FaceClassifier {
    boolean containsFace(byte[] image) throws ClassificationException;
}
