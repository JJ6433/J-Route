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
            @RequestParam(value = "checkin", required = false) String checkin,   
            @RequestParam(value = "adults", required = false) String adults,     
            @RequestParam("category") String category,
            
            // 💡 [新規追加データ] (エラー防止用required=false)
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "checkOut", required = false) String checkOut,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "details", required = false) String details,
            Model model) {

        // 予約基本情報転送
        model.addAttribute("itemName", itemName);
        model.addAttribute("price", price);
        model.addAttribute("checkin", checkin);
        model.addAttribute("adults", adults);
        model.addAttribute("category", category); 
        
        // 💡 新規追加データ転送
        model.addAttribute("imageUrl", imageUrl);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("address", address);
        model.addAttribute("details", details);

        return "payment/checkout"; // checkout.html表示
    }
}