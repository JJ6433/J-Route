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
        
        // 💡 찜목록 총합 변수들은 지웠습니다. (화면에서 선택할 거니까요!)
        int resFlightTotal = 0; 
        int resHotelTotal = 0;  
        
        BudgetDto myBudget = null; 
        List<WishlistDto> myWishlists = new ArrayList<>(); // 💡 찜목록을 통째로 담을 리스트

        if (userDetails != null) {
            UserDto user = userService.findByUsername(userDetails.getUsername()).orElse(null);
            if (user != null) {
                Long userId = user.getUserId();

                // 1. DB에서 이전에 저장해둔 내 예산 불러오기!
                myBudget = budgetService.getMyBudget(userId);

                // 💡 2. 찜목록 데이터 합산 로직 삭제 -> 리스트 통째로 가져오기
                myWishlists = wishlistService.getWishlistByUserId(userId);

                // 3. 예약/결제 내역 데이터 합산 (이미 결제한 건 모두 합산)
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

        // 화면으로 데이터 넘기기
        model.addAttribute("myBudget", myBudget); 
        model.addAttribute("myWishlists", myWishlists); // 💡 찜목록 전체 리스트 전송!
        model.addAttribute("resFlightTotal", resFlightTotal);
        model.addAttribute("resHotelTotal", resHotelTotal);

        return "exchange/exchange"; 
    }

    // 💡 화면에서 [저장] 버튼을 누를 때 통신할 API 주소!
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