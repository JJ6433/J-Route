package com.blog.controller;

import com.blog.dto.ReservationDto;
import com.blog.dto.UserDto;
import com.blog.service.ReservationService;
import com.blog.service.UserService; // 💡 UserService 임포트 추가
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {

    private final ReservationService reservationService;
    private final UserService userService; // 💡 UserService 주입

    // 💡 생성자에 UserService 추가
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
                // 💡 DB에서 현재 로그인한 유저의 전체 정보(UserDto)를 싹 가져옵니다.
                UserDto user = userService.findByUsername(userDetails.getUsername()).orElse(null);
                
                if (user != null) {
                    // 💡 진짜 고유번호(숫자 id)를 예약 내역에 세팅!
                    dto.setUserId(user.getUserId());
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "로그인이 필요합니다."));
            }

            // DB에 저장
            reservationService.saveReservation(dto);
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}