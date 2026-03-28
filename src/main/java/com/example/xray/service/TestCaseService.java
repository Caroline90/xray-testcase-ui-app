package com.example.xray.service;

import com.example.xray.controller.ImportResult;
import com.example.xray.model.*;
import com.example.xray.repository.TestCaseRepository;
import com.opencsv.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class TestCaseService {

    private final TestCaseRepository repo;

    public TestCaseService(TestCaseRepository repo) {
        this.repo = repo;
    }

    public List<TestCase> findAll() { return repo.findAll(); }

    public TestCase find(Long id) { return repo.findById(id).orElseThrow(); }

    public TestCase save(TestCase tc) { return repo.save(tc); }

    public void delete(Long id) { repo.deleteById(id); }

    // ✅ Xray CSV export
    public String exportCsv(List<TestCase> testCases) {
        StringWriter writer = new StringWriter();
        CSVWriter csv = new CSVWriter(writer);

        csv.writeNext(new String[]{
                "Test ID","Issue Key","Issue Type","Summary",
                "Description","Test Type","Component","Labels",
                "Affects Version/s","Fix Version/s","Execute In",
                "Action","Data","Result","Precondition"
        });

        int id = 1;

        for (TestCase tc : testCases) {
            boolean first = true;

            for (TestStep s : tc.getSteps()) {
                if (first) {
                    csv.writeNext(new String[]{
                            String.valueOf(id),"","Test",tc.getSummary(),
                            tc.getDescription(),tc.getTestType(),tc.getComponent(),tc.getLabels(),
                            tc.getAffectedVersion(),tc.getFixVersion(),tc.getExecuteIn(),
                            s.getAction(),s.getData(),s.getExpected(),
                            tc.getPrecondition()
                    });
                    first = false;
                } else {
                    csv.writeNext(new String[]{
                            String.valueOf(id),"","","","","","","","","",
                            s.getAction(),s.getData(),s.getExpected(),""
                    });
                }
            }
            id++;
        }
        return writer.toString();
    }

    // ✅ CSV import
    public List<TestCase> importCsv(InputStream input) throws Exception {
        List<TestCase> result = new ArrayList<>();

        CSVReader reader = new CSVReader(new InputStreamReader(input));
        List<String[]> rows = reader.readAll();

        Map<String, TestCase> map = new LinkedHashMap<>();

        for (int i = 1; i < rows.size(); i++) {
            String[] r = rows.get(i);
            String id = r[0];
            boolean hasExtendedColumns = r.length >= 15;

            TestCase tc = map.computeIfAbsent(id, k -> {
                TestCase t = new TestCase();
                t.setSummary(r[3]);
                if (hasExtendedColumns) {
                    t.setDescription(r[4]);
                    t.setTestType(r[5]);
                    t.setComponent(r[6]);
                    t.setLabels(r[7]);
                    t.setAffectedVersion(r[8]);
                    t.setFixVersion(r[9]);
                    t.setExecuteIn(r[10]);
                    t.setPrecondition(r[14]);
                } else {
                    t.setTestType(r[4]);
                    t.setComponent(r[5]);
                    t.setPrecondition(r[9]);
                }
                return t;
            });

            TestStep s = new TestStep();
            if (hasExtendedColumns) {
                s.setAction(r[11]);
                s.setData(r[12]);
                s.setExpected(r[13]);
            } else {
                s.setAction(r[6]);
                s.setData(r[7]);
                s.setExpected(r[8]);
            }

            tc.addStep(s);
        }

        return new ArrayList<>(map.values());
    }

    public ImportResult importCsvWithValidation(InputStream input) throws Exception {

        ImportResult result = new ImportResult();

        CSVReader reader = new CSVReader(new InputStreamReader(input));
        List<String[]> rows = reader.readAll();

        Map<String, TestCase> map = new LinkedHashMap<>();

        for (int i = 1; i < rows.size(); i++) {
            String[] r = rows.get(i);

            try {
                boolean hasExtendedColumns = r.length >= 15;
                if (r.length < 10) {
                    result.getErrors().add("Row " + (i+1) + ": invalid column count");
                    continue;
                }

                String id = r[0];

                TestCase tc = map.computeIfAbsent(id, k -> {
                    TestCase t = new TestCase();
                    t.setSummary(r[3]);
                    if (hasExtendedColumns) {
                        t.setDescription(r[4]);
                        t.setTestType(r[5]);
                        t.setComponent(r[6]);
                        t.setLabels(r[7]);
                        t.setAffectedVersion(r[8]);
                        t.setFixVersion(r[9]);
                        t.setExecuteIn(r[10]);
                        t.setPrecondition(r[14]);
                    } else {
                        t.setTestType(r[4]);
                        t.setComponent(r[5]);
                        t.setPrecondition(r[9]);
                    }
                    return t;
                });

                TestStep s = new TestStep();

                String action = hasExtendedColumns ? r[11] : r[6];
                if (action == null || action.isBlank()) {
                    result.getErrors().add("Row " + (i+1) + ": missing action");
                    continue;
                }

                s.setAction(action);
                s.setData(hasExtendedColumns ? r[12] : r[7]);
                s.setExpected(hasExtendedColumns ? r[13] : r[8]);

                tc.addStep(s);

            } catch (Exception e) {
                result.getErrors().add("Row " + (i+1) + ": " + e.getMessage());
            }
        }

        map.values().forEach(tc -> {
            tc.getSteps().forEach(step -> step.setTestCase(tc));
            result.getTestCases().add(repo.save(tc));
        });

        return result;
    }
}
