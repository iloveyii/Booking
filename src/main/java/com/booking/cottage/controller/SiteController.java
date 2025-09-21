package com.booking.cottage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SiteController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("name", "index page");
        return "login";
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("name", "index page");
        return "index";
    }

    @GetMapping("/details/{id}")
    public String details(Model model, @PathVariable Long id) {
        model.addAttribute("name", "details page for id: " + id);
        return "details";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("currentPage", "dashboard");
        return "dashboard";
    }

    @GetMapping("/dashboard/bookings")
    public String bookings(Model model) {
        model.addAttribute("currentPage", "bookings");
        return "bookings";
    }
}
