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
            
            // 💡 [새로 추가된 데이터들] (에러 방지를 위해 required = false 처리)
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "checkOut", required = false) String checkOut,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "details", required = false) String details,
            Model model) {

        // 넘겨받은 기본 예약 정보를 결제 화면(HTML)으로 그대로 전달합니다.
        model.addAttribute("itemName", itemName);
        model.addAttribute("price", price);
        model.addAttribute("checkin", checkin);
        model.addAttribute("adults", adults);
        model.addAttribute("category", category); 
        
        // 💡 새로 추가된 데이터들도 결제 화면으로 안전하게 넘겨줍니다.
        model.addAttribute("imageUrl", imageUrl);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("address", address);
        model.addAttribute("details", details);

        return "payment/checkout"; // payment 폴더 안의 checkout.html 열기
    }
}