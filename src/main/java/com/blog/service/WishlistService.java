package com.blog.service;

import com.blog.dto.WishlistDto;
import com.blog.mapper.WishlistMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 찜하기 서비스
 * 찜 목록 조회, 추가, 삭제, 이미 찜 여부 확인 (여행지 및 API 상품 공통)
 */
@Service
public class WishlistService {

	private final WishlistMapper wishlistMapper;

	public WishlistService(WishlistMapper wishlistMapper) {
		this.wishlistMapper = wishlistMapper;
	}

	// ==========================================
	// 1. 공통 기능
	// ==========================================
	@Transactional(readOnly = true)
	public List<WishlistDto> getWishlistByUserId(Long userId) {
		return wishlistMapper.findByUserId(userId);
	}

	// ==========================================
	// 2. 기존: '여행지(Place)' 전용 메서드
	// ==========================================
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
		dto.setCategory("PLACE"); // 💡 기존 여행지도 카테고리를 명시하도록 한 줄 추가!
		wishlistMapper.insertWishlist(dto);
	}

	@Transactional
	public void removeWish(Long userId, Long placeId) {
		wishlistMapper.deleteByUserAndPlace(userId, placeId);
	}

	// ==========================================
	// 3. 신규: '숙소/항공권(API)' 전용 메서드
	// ==========================================
	@Transactional(readOnly = true)
	public boolean isApiWishlisted(Long userId, String apiId, String category) {
		return wishlistMapper.findByUserAndApi(userId, apiId, category).isPresent();
	}

	@Transactional
	public void addWishlist(WishlistDto dto) {
		// 💡 중복 방지: 이미 찜한 API 상품인지 먼저 확인합니다.
		if (wishlistMapper.findByUserAndApi(dto.getUserId(), dto.getApiId(), dto.getCategory()).isPresent()) {
			return; // 이미 찜함
		}
		wishlistMapper.insertWishlist(dto);
	}

	@Transactional
	public void removeApiWishlist(Long userId, String apiId, String category) {
		wishlistMapper.deleteByUserAndApi(userId, apiId, category);
	}
}