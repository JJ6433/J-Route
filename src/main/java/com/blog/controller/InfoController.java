package com.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/info")
public class InfoController {

    @GetMapping("/team")
    public String team() {
        return "info/team";
    }

    @GetMapping("/terms")
    public String terms() {
        return "info/terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "info/privacy";
    }

    @GetMapping("/contact")
    public String contact() {
        return "info/contact";
    }
}
