package com.blog.controller;

import com.blog.dto.BudgetDto;
import com.blog.dto.ExchangeDto;
import com.blog.dto.ReservationDto;
import com.blog.dto.UserDto;
import com.blog.dto.WishlistDto;
import com.blog.service.BudgetService;
import com.blog.service.ExchangeService;
import com.blog.service.ReservationService;
import com.blog.service.UserService;
import com.blog.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final WishlistService wishlistService;
    private final UserService userService;
    private final ReservationService reservationService;
    private final BudgetService budgetService; 

    public ExchangeController(ExchangeService exchangeService, WishlistService wishlistService, UserService userService, ReservationService reservationService, BudgetService budgetService) {
        this.exchangeService = exchangeService;
        this.wishlistService = wishlistService;
        this.userService = userService;
        this.reservationService = reservationService;
        this.budgetService = budgetService;
    }

    @GetMapping("/budget")
    public String showExchangeAndBudgetPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("activeMenu", "exchange");
        
        ExchangeDto exchangeRate = exchangeService.getLatestExchangeRate();
        model.addAttribute("exchange", exchangeRate);
        
        // 💡 画面選択のためウィッシュリスト合算変数削除
        int resFlightTotal = 0; 
        int resHotelTotal = 0;  
        
        BudgetDto myBudget = null; 
        List<WishlistDto> myWishlists = new ArrayList<>(); // 💡 ウィッシュリスト保持用リスト

        if (userDetails != null) {
            UserDto user = userService.findByUsername(userDetails.getUsername()).orElse(null);
            if (user != null) {
                Long userId = user.getUserId();

                // 1. DBから保存済み予算読み込み
                myBudget = budgetService.getMyBudget(userId);

                // 💡 2. ウィッシュリスト合算ロジック削除 -> リスト直接取得
                myWishlists = wishlistService.getWishlistByUserId(userId);

                // 3. 予約・決済内訳データ合算 (決済済全合算)
                List<ReservationDto> myReservations = reservationService.getAllReservations(userId);
                for (ReservationDto res : myReservations) {
                    if ("FLIGHT".equals(res.getCategory())) {
                        resFlightTotal += res.getAmount();
                    } else if ("HOTEL".equals(res.getCategory())) {
                        resHotelTotal += res.getAmount();
                    }
                }
            }
        }

        // 画面データ伝達
        model.addAttribute("myBudget", myBudget); 
        model.addAttribute("myWishlists", myWishlists); // 💡 ウィッシュリスト全体データ転送
        model.addAttribute("resFlightTotal", resFlightTotal);
        model.addAttribute("resHotelTotal", resHotelTotal);

        return "exchange/exchange"; 
    }

    // 💡 [保存]ボタン通信用API
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> saveBudget(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BudgetDto budgetDto) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("status", "unauthorized"));
        }
        
        UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
        budgetDto.setUserId(user.getUserId()); 
        
        budgetService.saveMyBudget(budgetDto); 
        
        return ResponseEntity.ok(Map.of("status", "success"));
    }
}