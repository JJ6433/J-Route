package com.blog.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GourmetController {

    @Value("${maps.api.key}")
    private String apiKey;

    @GetMapping("/trip/gourmet")
    public String showGourmetSearch(Model model) {
        model.addAttribute("activeMenu", "gourmet");
        model.addAttribute("apiKey", apiKey);
        return "trip/gourmet";
    }
}
