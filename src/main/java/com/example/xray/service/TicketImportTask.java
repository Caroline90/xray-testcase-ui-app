package com.example.xray.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TicketImportTask {

    public static void main(String[] args) throws Exception {
        String ticketKey = System.getProperty("ticketKey");
        String baseUrl = System.getProperty("baseUrl", "http://localhost:9090");
        String stepsFile = System.getProperty("stepsFile", "");

        if (ticketKey == null || ticketKey.isBlank()) {
            throw new IllegalArgumentException("Provide -DticketKey=TEST-2439");
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("ticketKey", ticketKey);
        payload.put("steps", loadSteps(stepsFile));

        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/testcases/import-ticket"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            throw new RuntimeException("Import failed: " + response.statusCode() + " - " + response.body());
        }

        System.out.println("Imported ticket " + ticketKey + " successfully.");
        System.out.println(response.body());
    }

    private static List<Map<String, String>> loadSteps(String stepsFile) throws IOException {
        List<Map<String, String>> steps = new ArrayList<>();
        if (stepsFile == null || stepsFile.isBlank()) {
            return steps;
        }

        for (String line : Files.readAllLines(Path.of(stepsFile))) {
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            String[] cols = line.split(",", -1);
            if (cols.length < 3) {
                continue;
            }
            Map<String, String> step = new LinkedHashMap<>();
            step.put("action", cols[0].trim());
            step.put("data", cols[1].trim());
            step.put("expected", cols[2].trim());
            steps.add(step);
        }
        return steps;
    }
}
