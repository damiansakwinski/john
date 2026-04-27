package com.animalhotels.john.photoclassification;

import java.net.URI;

public interface ImageDownloader {
    byte[] download(URI uri) throws ImageDownloadException;
}
