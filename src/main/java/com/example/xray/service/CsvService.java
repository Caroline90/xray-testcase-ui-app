package com.example.xray.service;

import com.example.xray.model.TestCase;
import com.example.xray.model.TestStep;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CsvService {

    public String generateCsv(List<TestCase> testCases) {

        StringWriter writer = new StringWriter();
        CSVWriter csv = new CSVWriter(writer);

        // Header (Xray format)
        csv.writeNext(new String[]{
                "Test ID",
                "Issue Key",
                "Issue Type",
                "Summary",
                "Test Type",
                "Precondition type",
                "Component",
                "Action",
                "Data",
                "Result",
                "Precondition specification"
        });

        AtomicInteger testId = new AtomicInteger(1);

        for (TestCase tc : testCases) {

            int currentId = testId.getAndIncrement();
            boolean firstStep = true;

            for (TestStep step : tc.getSteps()) {

                if (firstStep) {
                    // First row = full test case info
                    csv.writeNext(new String[]{
                            String.valueOf(currentId),
                            "",                     // Issue Key
                            "Test",
                            tc.getSummary(),
                            "Manual",
                            "",                     // Precondition type
                            "",                     // Component
                            step.getAction(),
                            "",                     // Data (optional)
                            step.getExpected(),
                            tc.getPrecondition()
                    });
                    firstStep = false;

                } else {
                    // Next rows = only steps
                    csv.writeNext(new String[]{
                            String.valueOf(currentId),
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            step.getAction(),
                            "",
                            step.getExpected(),
                            ""
                    });
                }
            }
        }

        return writer.toString();
    }
}