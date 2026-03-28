package com.example.xray.controller;

import com.example.xray.model.*;
import com.example.xray.service.TestCaseService;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {
    private final TestCaseService service;
    public WebController(TestCaseService service) { this.service = service; }

    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("cases", service.findAll());
        return "index";
    }

}
