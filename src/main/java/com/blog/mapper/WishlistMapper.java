package com.blog.mapper;

import com.blog.dto.WishlistDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * お気に入り MyBatis Mapper
 * お気に入りリスト照会、追加、削除、重複チェック（旅行先及びAPI商品共通）
 */
@Mapper
public interface WishlistMapper {

    // ユーザー別お気に入りリスト照会
    List<WishlistDto> findByUserId(Long userId);

    // お気に入り追加
    void insertWishlist(WishlistDto wishlistDto);
    
    // 既存: 旅行先専用
    int deleteByUserAndPlace(@Param("userId") Long userId, @Param("placeId") Long placeId);
    Optional<WishlistDto> findByUserAndPlace(@Param("userId") Long userId, @Param("placeId") Long placeId);

    // 新規: 宿泊/航空券専用
    // 特定商品のお気に入り解除
    int deleteByUserAndApi(@Param("userId") Long userId, @Param("apiId") String apiId, @Param("category") String category);

    // お気に入り登録確認
    Optional<WishlistDto> findByUserAndApi(@Param("userId") Long userId, @Param("apiId") String apiId, @Param("category") String category);
}