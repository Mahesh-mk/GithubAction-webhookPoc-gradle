package com.example.webhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HppAssetPayloadProcessor {


    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            throw new RuntimeException("payload.json path missing");
        }

        String payloadPath = args[0];

        ObjectMapper mapper = new ObjectMapper();

        JsonNode assetPayload =
                mapper.readTree(new File(payloadPath));

        validatePayload(assetPayload);

        JsonNode files = assetPayload.get("files");

        for (JsonNode file : files) {

            String path = file.get("path").asText();

            String content = file.get("content").asText();

            validateFilePath(path);

            createFile(path, content);

            System.out.println("Created file: " + path);
        }

        System.out.println("Payload processing completed");
    }

    private static void validatePayload(JsonNode assetPayload) {

        if (assetPayload.get("projectId") == null) {
            throw new RuntimeException("projectId missing");
        }

        if (assetPayload.get("files") == null || assetPayload.get("files").isEmpty()) {

            throw new RuntimeException("files missing");
        }
    }

    private static void validateFilePath(String path) {
        if (path.contains("..")) {
            throw new RuntimeException("Invalid file path");
        }
    }

    private static void createFile(String path, String content) throws Exception {

        Path filePath = Paths.get(path);

        Files.createDirectories(filePath.getParent());

        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
        }
    }
}