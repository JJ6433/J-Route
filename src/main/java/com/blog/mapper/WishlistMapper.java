package com.blog.mapper;

import com.blog.dto.WishlistDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 찜하기 MyBatis Mapper
 * 찜 목록 조회, 추가, 삭제, 중복 체크
 */
@Mapper
public interface WishlistMapper {

	List<WishlistDto> findByUserId(Long userId);

	void insertWishlist(WishlistDto wishlistDto);

	int deleteByUserAndPlace(@Param("userId") Long userId, @Param("placeId") Long placeId);

	Optional<WishlistDto> findByUserAndPlace(@Param("userId") Long userId, @Param("placeId") Long placeId);
}
