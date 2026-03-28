package com.example.xray.model;

import jakarta.persistence.*;

@Entity
public class TestStep {

    @Id
    @GeneratedValue
    private Long id;

    private String action;
    private String data;
    private String expected;

    @ManyToOne
    private TestCase testCase;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getExpected() { return expected; }
    public void setExpected(String expected) { this.expected = expected; }

    public void setTestCase(TestCase testCase) { this.testCase = testCase; }
}