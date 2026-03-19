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

@RestController // JSONデータ返却用Controller
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
        if (userDetails == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UserDto user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        boolean isPlace = "PLACE".equalsIgnoreCase(requestDto.getCategory());
        boolean isAlreadyWished;

        if (isPlace) {
            // For PLACE, apiId in request is actually placeId
            if (requestDto.getPlaceId() == null && requestDto.getApiId() != null) {
                try {
                    requestDto.setPlaceId(Long.parseLong(requestDto.getApiId()));
                } catch (Exception ignored) {
                }
            }
            isAlreadyWished = wishlistService.isWished(user.getUserId(), requestDto.getPlaceId());
        } else {
            isAlreadyWished = wishlistService.isApiWishlisted(user.getUserId(), requestDto.getApiId(),
                    requestDto.getCategory());
        }

        Map<String, Object> response = new HashMap<>();
        try {
            if (isAlreadyWished) {
                if (isPlace)
                    wishlistService.removeWish(user.getUserId(), requestDto.getPlaceId());
                else
                    wishlistService.removeApiWishlist(user.getUserId(), requestDto.getApiId(),
                            requestDto.getCategory());
            } else {
                requestDto.setUserId(user.getUserId());
                wishlistService.addWishlist(requestDto);
            }
            response.put("status", "success");
            response.put("wished", !isAlreadyWished);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeWishlist(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody WishlistDto requestDto) {
        if (userDetails == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UserDto user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        boolean isPlace = "PLACE".equalsIgnoreCase(requestDto.getCategory());

        if (isPlace) {
            Long pId = requestDto.getPlaceId();
            if (pId == null && requestDto.getApiId() != null) {
                try {
                    pId = Long.parseLong(requestDto.getApiId());
                } catch (Exception ignored) {
                }
            }
            if (pId != null)
                wishlistService.removeWish(user.getUserId(), pId);

            // Also try removing by apiId if category is PLACE to clean up ghost items
            if (requestDto.getApiId() != null) {
                wishlistService.removeApiWishlist(user.getUserId(), requestDto.getApiId(), "PLACE");
            }
        } else {
            wishlistService.removeApiWishlist(user.getUserId(), requestDto.getApiId(), requestDto.getCategory());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}