package com.blog.controller.admin;

import com.blog.dto.TransportDto;
import com.blog.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/transport")
public class AdminTransportController {

    @Autowired
    private TransportService transportService;

    // 管理者用リスト画面
    @GetMapping("/list")
    public String list(Model model) {
        List<TransportDto> passList = transportService.getAllPasses();
        model.addAttribute("passList", passList);
        return "admin/transport/list";
    }

    // 登録・修正フォーム画面
    // 💡 Spring Boot 3.2の仕様に合わせ、(name = "id") を明示的に追加しました！
    @GetMapping({"/form", "/form/{id}"})
    public String form(@PathVariable(name = "id", required = false) Long id, Model model) {
        TransportDto pass = (id != null) ? transportService.getPassById(id) : new TransportDto();
        model.addAttribute("pass", pass);
        return "admin/transport/form";
    }

    // データを保存
    @PostMapping("/save")
    public String save(@ModelAttribute TransportDto pass) {
        transportService.savePass(pass);
        return "redirect:/admin/transport/list";
    }

    // データを削除
    // 💡 ここも ("id") を明示的に指定します
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        transportService.deletePass(id);
        return "redirect:/admin/transport/list";
    }
}