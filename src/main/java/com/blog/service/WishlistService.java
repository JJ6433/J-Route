package com.blog.service;

import com.blog.dto.WishlistDto;
import com.blog.mapper.WishlistMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 찜하기 서비스
 * 찜 목록 조회, 추가, 삭제, 이미 찜 여부 확인
 */
@Service
public class WishlistService {

	private final WishlistMapper wishlistMapper;

	public WishlistService(WishlistMapper wishlistMapper) {
		this.wishlistMapper = wishlistMapper;
	}

	@Transactional(readOnly = true)
	public List<WishlistDto> getWishlistByUserId(Long userId) {
		return wishlistMapper.findByUserId(userId);
	}

	@Transactional(readOnly = true)
	public boolean isWished(Long userId, Long placeId) {
		return wishlistMapper.findByUserAndPlace(userId, placeId).isPresent();
	}

	@Transactional
	public void addWish(Long userId, Long placeId) {
		if (wishlistMapper.findByUserAndPlace(userId, placeId).isPresent()) {
			return; // 이미 찜함
		}
		WishlistDto dto = new WishlistDto();
		dto.setUserId(userId);
		dto.setPlaceId(placeId);
		wishlistMapper.insertWishlist(dto);
	}

	@Transactional
	public void removeWish(Long userId, Long placeId) {
		wishlistMapper.deleteByUserAndPlace(userId, placeId);
	}
}
