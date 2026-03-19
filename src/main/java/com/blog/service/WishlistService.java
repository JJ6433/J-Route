package com.blog.service;

import com.blog.dto.WishlistDto;
import com.blog.mapper.WishlistMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * お気に入りサービス
 * お気に入りリスト照会、追加、削除、重複確認（旅行先及びAPI商品共通）
 */
@Service
public class WishlistService {

	private final WishlistMapper wishlistMapper;

	public WishlistService(WishlistMapper wishlistMapper) {
		this.wishlistMapper = wishlistMapper;
	}

	// ==========================================
	// 共通機能
	// ==========================================
	@Transactional(readOnly = true)
	public List<WishlistDto> getWishlistByUserId(Long userId) {
		return wishlistMapper.findByUserId(userId);
	}

	// ==========================================
	// 旅行先(Place)専用メソッド
	// ==========================================
	@Transactional(readOnly = true)
	public boolean isWished(Long userId, Long placeId) {
		return wishlistMapper.findByUserAndPlace(userId, placeId).isPresent();
	}

	@Transactional
	public void addWish(Long userId, Long placeId) {
		if (wishlistMapper.findByUserAndPlace(userId, placeId).isPresent()) {
			return; // 登録済み
		}
		WishlistDto dto = new WishlistDto();
		dto.setUserId(userId);
		dto.setPlaceId(placeId);
		dto.setCategory("PLACE"); // カテゴリ明示
		wishlistMapper.insertWishlist(dto);
	}

	@Transactional
	public void removeWish(Long userId, Long placeId) {
		wishlistMapper.deleteByUserAndPlace(userId, placeId);
	}

	// ==========================================
	// 宿泊/航空券(API)専用メソッド
	// ==========================================
	@Transactional(readOnly = true)
	public boolean isApiWishlisted(Long userId, String apiId, String category) {
		return wishlistMapper.findByUserAndApi(userId, apiId, category).isPresent();
	}

	@Transactional
	public void addWishlist(WishlistDto dto) {
		// 重複登録確認
		if (wishlistMapper.findByUserAndApi(dto.getUserId(), dto.getApiId(), dto.getCategory()).isPresent()) {
			return; // 登録済み
		}
		wishlistMapper.insertWishlist(dto);
	}

	@Transactional
	public void removeApiWishlist(Long userId, String apiId, String category) {
		wishlistMapper.deleteByUserAndApi(userId, apiId, category);
	}
}