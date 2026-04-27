package com.animalhotels.john.photoclassification;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;

@Component
public class BedrockFaceClassifier implements FaceClassifier {
    private final static String MODEL_ID = "eu.anthropic.claude-haiku-4-5-20251001-v1:0";
    private final static int MAX_TOKENS = 512;
    private final static int TEMPERATURE = 0;
    private final static String ANTHROPIC_VERSION = "bedrock-2023-05-31";
    private final static String PROMPT = """
        Does this image prominently show a human face? Respond with only "true" or "false".
        Respond "true" if a person's face is clearly visible, even if they are holding or
        posing with an animal. Respond "false" for: animals only, scenery, objects, images
        where no human face is visible, or images where a person appears but their face is
        not visible or is too small/blurred to identify.""";

    private final ObjectMapper objectMapper;
    private final BedrockRuntimeClient bedrockRuntimeClient;

    public BedrockFaceClassifier(
        ObjectMapper objectMapper,
        BedrockRuntimeClient bedrockRuntimeClient
    ) {
        this.objectMapper = objectMapper;
        this.bedrockRuntimeClient = bedrockRuntimeClient;
    }

    @Override
    public boolean containsFace(byte[] image) {
        try {
            BedrockRequest requestObjectNode = this.buildRequest(image);
            InvokeModelResponse response = this.bedrockRuntimeClient.invokeModel(request -> request
                .body(SdkBytes.fromUtf8String(this.objectMapper.writeValueAsString(requestObjectNode)))
                .modelId(MODEL_ID)
            );
            JsonNode responseBody = this.objectMapper.readTree(response.body().asUtf8String());

            return responseBody.at("/content/0/text").asBoolean();
        } catch (SdkClientException | IOException e) {
            throw new ClassificationException(e);
        }
    }

    private BedrockRequest buildRequest(byte[] image) throws IOException {
        String base64Image = Base64.getEncoder().encodeToString(image);
        String mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(image));

        return new BedrockRequest(ANTHROPIC_VERSION, MAX_TOKENS, TEMPERATURE, List.of(
            new Message("user", List.of(
                new ImageContent("image", new ImageSource("base64", mimeType, base64Image)),
                new TextContent("text", PROMPT)
            ))
        ));
    }
}
