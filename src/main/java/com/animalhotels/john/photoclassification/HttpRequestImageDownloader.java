package com.animalhotels.john.photoclassification;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
public class HttpRequestImageDownloader implements ImageDownloader {
    private final RestClient restClient;

    public HttpRequestImageDownloader(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public byte[] download(URI uri) {
        try {
            return this.restClient.get()
                .uri(uri)
                .retrieve()
                .body(byte[].class);
        } catch (Exception e) {
            throw new ImageDownloadException(e);
        }
    }
}
