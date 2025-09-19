package com.booking.cottage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("name", "index page");
        return "index";
    }
    @GetMapping("/details")
    public String details(Model model) {
        model.addAttribute("name", "details page");
        return "details";
    }
}
