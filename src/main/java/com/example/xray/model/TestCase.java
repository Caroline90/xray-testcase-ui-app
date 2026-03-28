package com.example.xray.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TestCase {

    @Id
    @GeneratedValue
    private Long id;

    private String issueKey;
    private String summary;
    private String description;
    private String precondition;
    private String testType;
    private String component;
    private String affectedVersion;
    private String labels;
    private String fixVersion;
    private String executeIn;

    @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestStep> steps = new ArrayList<>();

    public void addStep(TestStep step) {
        step.setTestCase(this);
        steps.add(step);
    }

    public Long getId() { return id; }

    public String getIssueKey() { return issueKey; }
    public void setIssueKey(String issueKey) { this.issueKey = issueKey; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPrecondition() { return precondition; }
    public void setPrecondition(String precondition) { this.precondition = precondition; }

    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }

    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }

    public String getAffectedVersion() { return affectedVersion; }
    public void setAffectedVersion(String affectedVersion) { this.affectedVersion = affectedVersion; }

    public String getLabels() { return labels; }
    public void setLabels(String labels) { this.labels = labels; }

    public String getFixVersion() { return fixVersion; }
    public void setFixVersion(String fixVersion) { this.fixVersion = fixVersion; }

    public String getExecuteIn() { return executeIn; }
    public void setExecuteIn(String executeIn) { this.executeIn = executeIn; }

    public List<TestStep> getSteps() { return steps; }
}
