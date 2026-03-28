package com.example.xray.controller;

import com.example.xray.model.*;
import com.example.xray.service.JiraTicketImportService;
import com.example.xray.service.TestCaseService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/api/testcases")
public class TestCaseApiController {

    private final TestCaseService service;
    private final JiraTicketImportService jiraTicketImportService;

    public TestCaseApiController(TestCaseService service, JiraTicketImportService jiraTicketImportService) {
        this.service = service;
        this.jiraTicketImportService = jiraTicketImportService;
    }

    @GetMapping
    public List<TestCase> all() {
        return service.findAll();
    }

    @PostMapping
    public TestCase create(@RequestBody TestCase tc) {
        tc.getSteps().forEach(s -> s.setTestCase(tc));
        return service.save(tc);
    }

    @PutMapping("/{id}")
    public TestCase update(@PathVariable Long id, @RequestBody TestCase tc) {
        TestCase existing = service.find(id);

        existing.setIssueKey(tc.getIssueKey());
        existing.setSummary(tc.getSummary());
        existing.setDescription(tc.getDescription());
        existing.setPrecondition(tc.getPrecondition());
        existing.setTestType(tc.getTestType());
        existing.setComponent(tc.getComponent());
        existing.setAffectedVersion(tc.getAffectedVersion());
        existing.setLabels(tc.getLabels());
        existing.setFixVersion(tc.getFixVersion());
        existing.setExecuteIn(tc.getExecuteIn());

        existing.getSteps().clear();

        tc.getSteps().forEach(s -> {
            s.setTestCase(existing);
            existing.getSteps().add(s);
        });

        return service.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/export")
    public ResponseEntity<String> export() {
        String csv = service.exportCsv(service.findAll());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=testcases.csv")
                .body(csv);
    }

    @PostMapping("/import-ticket")
    public TestCase importByTicket(@RequestBody ImportTicketRequest request) {
        return jiraTicketImportService.importByTicket(request.getTicketKey(), request.getSteps());
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ImportResult importCsv(@RequestParam("file") MultipartFile file) throws Exception {
        return service.importCsvWithValidation(file.getInputStream());
    }
}
