package com.blog.controller;

import com.blog.dto.TransportDto;
import com.blog.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/transport")
public class TransportController {

    @Autowired
    private TransportService transportService;

    // APIキー読込
    @Value("${maps.api.key}")
    private String mapsApiKey;

    // パス画面表示
    @GetMapping
    public String transportMain(Model model) {
        
        // サイドバー活性化
        model.addAttribute("activeMenu", "transport");
        
        // パスデータ取得
        List<TransportDto> passList = transportService.getAllPasses();
        model.addAttribute("passList", passList);

        // APIキービュー伝達
        model.addAttribute("googleMapsApiKey", mapsApiKey);
        
        return "transport/main";
    }
}