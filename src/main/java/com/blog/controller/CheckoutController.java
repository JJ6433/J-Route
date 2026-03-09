package com.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {

    @GetMapping("/checkout")
    public String showCheckoutPage(
            @RequestParam("itemName") String itemName,
            @RequestParam(value = "price", defaultValue = "0") int price,
            @RequestParam("checkin") String checkin,   
            @RequestParam("adults") String adults,     
            @RequestParam("category") String category, // 💡 새로 추가된 카테고리 파라미터
            Model model) {

        // 넘겨받은 예약 정보를 결제 화면(HTML)으로 그대로 전달합니다.
        model.addAttribute("itemName", itemName);
        model.addAttribute("price", price);
        model.addAttribute("checkin", checkin);
        model.addAttribute("adults", adults);
        model.addAttribute("category", category); // 💡 HTML로 카테고리 값도 넘겨줌

        return "payment/checkout"; // payment 폴더 안의 checkout.html 열기
    }
}