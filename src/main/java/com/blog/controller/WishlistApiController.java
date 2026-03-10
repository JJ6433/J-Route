package com.blog.controller;

import com.blog.dto.UserDto;
import com.blog.dto.WishlistDto;
import com.blog.service.UserService;
import com.blog.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController // 일반 @Controller와 달리 화면(HTML)을 반환하지 않고, 데이터(JSON)만 반환합니다.
@RequestMapping("/api/wishlist")
public class WishlistApiController {

    private final WishlistService wishlistService;
    private final UserService userService;

    public WishlistApiController(WishlistService wishlistService, UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleWishlist(@AuthenticationPrincipal UserDetails userDetails,
                                                              @RequestBody WishlistDto requestDto) {
        // 1. 로그인 확인 (안 되어있으면 401 에러 반환 -> JS에서 로그인 창으로 보냄)
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. 현재 로그인한 유저 정보 가져오기
        UserDto user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3. 자바스크립트에서 넘어온 DTO에 유저 ID 세팅
        requestDto.setUserId(user.getUserId());

        // 💡 4. DB에 이미 찜이 되어 있는지 확인 (Service에 만들어둔 실제 메서드명 + 파라미터 적용!)
        // apiId뿐만 아니라 category("HOTEL")까지 같이 넘겨서 정확히 찾습니다.
        boolean isAlreadyWished = wishlistService.isApiWishlisted(user.getUserId(), requestDto.getApiId(), requestDto.getCategory());

        // 💡 5. 찜 상태에 따라 추가 또는 삭제 실행 (마찬가지로 Service의 실제 메서드명 적용!)
        if (isAlreadyWished) {
            wishlistService.removeApiWishlist(user.getUserId(), requestDto.getApiId(), requestDto.getCategory());
        } else {
            wishlistService.addWishlist(requestDto); 
        }

        // 6. 프론트엔드에 전달할 결과값 (true면 꽉 찬 하트, false면 빈 하트로 바뀜)
        Map<String, Object> response = new HashMap<>();
        response.put("wished", !isAlreadyWished); 

        return ResponseEntity.ok(response);
    }
}