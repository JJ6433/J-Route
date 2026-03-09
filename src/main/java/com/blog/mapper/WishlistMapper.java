package com.blog.mapper;

import com.blog.dto.WishlistDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 찜하기 MyBatis Mapper
 * 찜 목록 조회, 추가, 삭제, 중복 체크 (여행지 및 API 상품 공통)
 */
@Mapper
public interface WishlistMapper {

    // 1. 유저별 전체 찜 목록 조회
    List<WishlistDto> findByUserId(Long userId);

    // 2. 찜 추가 (공통)
    void insertWishlist(WishlistDto wishlistDto);
    
    // --- 기존: 여행지(Place) 전용 ---
    int deleteByUserAndPlace(@Param("userId") Long userId, @Param("placeId") Long placeId);
    Optional<WishlistDto> findByUserAndPlace(@Param("userId") Long userId, @Param("placeId") Long placeId);

    // --- 💡 신규: 숙소/항공권(API) 전용 ---
    // 특정 유저가 특정 카테고리(HOTEL/FLIGHT)의 특정 상품(apiId)을 찜 취소할 때 사용
    int deleteByUserAndApi(@Param("userId") Long userId, @Param("apiId") String apiId, @Param("category") String category);

    // 특정 유저가 이미 찜한 상품인지 확인할 때 사용 (Service의 isApiWishlisted에서 호출)
    Optional<WishlistDto> findByUserAndApi(@Param("userId") Long userId, @Param("apiId") String apiId, @Param("category") String category);
}