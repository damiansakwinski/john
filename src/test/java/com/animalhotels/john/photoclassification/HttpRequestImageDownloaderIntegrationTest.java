package com.animalhotels.john.photoclassification;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

@SpringBootTest
class HttpRequestImageDownloaderIntegrationTest {
    @Autowired
    private HttpRequestImageDownloader sut;

    @Test
    void downloadsAnImage() throws URISyntaxException, IOException {
        byte[] actual = this.sut.download(new URI("https://caelejzzba.cloudimg.io/_ahmedia_/media/img-5/343-6/9ecee/img-5343-69eceeba67a64070714819.jpeg"));
        Assertions.assertThat((URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(actual)))
            .startsWith("image/"));
    }

    @Test
    void throwsOnUnreachableUrl() {
        Assertions.assertThatThrownBy(() -> this.sut.download(new URI("https://asdjiasdjdiashida.com")))
            .isInstanceOf(ImageDownloadException.class);
    }
}
