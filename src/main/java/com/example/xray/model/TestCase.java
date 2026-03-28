package com.example.xray.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TestCase {

    @Id
    @GeneratedValue
    private Long id;

    private String summary;
    private String precondition;
    private String testType;
    private String component;

    @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestStep> steps = new ArrayList<>();

    public void addStep(TestStep step) {
        step.setTestCase(this);
        steps.add(step);
    }

    public Long getId() { return id; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getPrecondition() { return precondition; }
    public void setPrecondition(String precondition) { this.precondition = precondition; }

    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }

    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }

    public List<TestStep> getSteps() { return steps; }
}