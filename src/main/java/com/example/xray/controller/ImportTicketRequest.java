package com.example.xray.controller;

import java.util.ArrayList;
import java.util.List;

public class ImportTicketRequest {
    private String ticketKey;
    private List<TicketStepRequest> steps = new ArrayList<>();

    public String getTicketKey() {
        return ticketKey;
    }

    public void setTicketKey(String ticketKey) {
        this.ticketKey = ticketKey;
    }

    public List<TicketStepRequest> getSteps() {
        return steps;
    }

    public void setSteps(List<TicketStepRequest> steps) {
        this.steps = steps;
    }
}
