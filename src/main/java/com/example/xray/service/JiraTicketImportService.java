package com.example.xray.service;

import com.example.xray.controller.TicketStepRequest;
import com.example.xray.model.TestCase;
import com.example.xray.model.TestStep;
import com.example.xray.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JiraTicketImportService {

    private final TestCaseRepository repository;
    private final RestClient restClient;
    private final boolean configured;

    public JiraTicketImportService(
            TestCaseRepository repository,
            @Value("${jira.base-url:}") String jiraBaseUrl,
            @Value("${jira.username:}") String jiraUsername,
            @Value("${jira.api-token:}") String jiraApiToken
    ) {
        this.repository = repository;

        this.configured = StringUtils.hasText(jiraBaseUrl) && StringUtils.hasText(jiraUsername) && StringUtils.hasText(jiraApiToken);

        if (configured) {
            String auth = Base64.getEncoder()
                    .encodeToString((jiraUsername + ":" + jiraApiToken).getBytes(StandardCharsets.UTF_8));

            this.restClient = RestClient.builder()
                    .baseUrl(jiraBaseUrl)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .build();
        } else {
            this.restClient = null;
        }
    }

    public TestCase importByTicket(String ticketKey, List<TicketStepRequest> steps) {
        if (!StringUtils.hasText(ticketKey)) {
            throw new IllegalArgumentException("ticketKey is required");
        }
        if (!configured) {
            throw new IllegalStateException("Set jira.base-url, jira.username and jira.api-token before importing by ticket.");
        }

        Map<String, Object> issue = restClient.get()
                .uri("/rest/api/2/issue/{key}", ticketKey)
                .retrieve()
                .body(Map.class);

        if (issue == null || issue.get("fields") == null) {
            throw new IllegalStateException("Jira response did not include fields for ticket " + ticketKey);
        }

        Map<String, Object> fields = (Map<String, Object>) issue.get("fields");

        TestCase testCase = new TestCase();
        testCase.setIssueKey(ticketKey);
        testCase.setSummary(asString(fields.get("summary")));
        testCase.setDescription(asString(fields.get("description")));
        testCase.setLabels(joinStrings((List<?>) fields.get("labels")));
        testCase.setComponent(joinNamed((List<?>) fields.get("components")));
        testCase.setAffectedVersion(joinNamed((List<?>) fields.get("versions")));
        testCase.setFixVersion(joinNamed((List<?>) fields.get("fixVersions")));
        testCase.setTestType("Manual");

        if (steps == null || steps.isEmpty()) {
            TestStep defaultStep = new TestStep();
            defaultStep.setAction("Imported from ticket " + ticketKey);
            defaultStep.setData("");
            defaultStep.setExpected("Test data imported successfully");
            testCase.addStep(defaultStep);
        } else {
            steps.stream()
                    .filter(Objects::nonNull)
                    .forEach(step -> {
                        TestStep item = new TestStep();
                        item.setAction(step.getAction());
                        item.setData(step.getData());
                        item.setExpected(step.getExpected());
                        testCase.addStep(item);
                    });
        }

        return repository.save(testCase);
    }

    private static String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static String joinStrings(List<?> values) {
        if (values == null) {
            return "";
        }
        return values.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private static String joinNamed(List<?> values) {
        if (values == null) {
            return "";
        }
        return values.stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(v -> asString(v.get("name")))
                .collect(Collectors.joining(","));
    }
}
