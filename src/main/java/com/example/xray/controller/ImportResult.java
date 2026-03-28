package com.example.xray.controller;

import com.example.xray.model.TestCase;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {
    private List<TestCase> testCases = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public List<TestCase> getTestCases() { return testCases; }
    public List<String> getErrors() { return errors; }
}