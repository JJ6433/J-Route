package com.blog.controller;

import com.blog.dto.ReservationDto;
import com.blog.dto.UserDto;
import com.blog.service.ReservationService;
import com.blog.service.UserService; // UserServiceインポート
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {

    private final ReservationService reservationService;
    private final UserService userService; // UserService注入

    // コンストラクタにUserService追加
    public PaymentApiController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(
            @RequestBody ReservationDto dto, 
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            if (userDetails != null) {
                // ログインユーザー全情報取得
                UserDto user = userService.findByUsername(userDetails.getUsername()).orElse(null);
                
                if (user != null) {
                    // 固有IDセッティング
                    dto.setUserId(user.getUserId());
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "ログインが必要です。"));
            }

            // DB保存
            reservationService.saveReservation(dto);
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}