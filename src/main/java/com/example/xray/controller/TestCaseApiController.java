package com.example.xray.controller;

import com.example.xray.model.*;
import com.example.xray.service.TestCaseService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/testcases")
public class TestCaseApiController {

    private final TestCaseService service;

    public TestCaseApiController(TestCaseService service) {
        this.service = service;
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

        existing.setSummary(tc.getSummary());
        existing.setPrecondition(tc.getPrecondition());
        existing.setTestType(tc.getTestType());
        existing.setComponent(tc.getComponent());

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

    @PostMapping("/import")
    public List<TestCase> importCsv(@RequestParam("file") MultipartFile file) throws Exception {
        return service.importCsv(file.getInputStream());
    }
}