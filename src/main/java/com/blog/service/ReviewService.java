package com.blog.service;

import com.blog.dto.ReviewDto;
import com.blog.mapper.ReviewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * レビューサービス
 * 旅行先別レビュー照会/登録/修正/削除、平均評価
 */
@Service
public class ReviewService {

	private final ReviewMapper reviewMapper;

	public ReviewService(ReviewMapper reviewMapper) {
		this.reviewMapper = reviewMapper;
	}

	@Transactional(readOnly = true)
	public List<ReviewDto> getByPlaceId(Long placeId) {
		return reviewMapper.findByPlaceId(placeId);
	}

	@Transactional(readOnly = true)
	public Double getAvgRating(Long placeId) {
		Double avg = reviewMapper.getAvgRatingByPlaceId(placeId);
		return avg != null ? avg : 0.0;
	}

	@Transactional(readOnly = true)
	public int getReviewCount(Long placeId) {
		return reviewMapper.countByPlaceId(placeId);
	}

	@Transactional
	public void addReview(ReviewDto reviewDto) {
		if (reviewDto.getRating() == null || reviewDto.getRating() < 1 || reviewDto.getRating() > 5) {
			throw new IllegalArgumentException("評価は1〜5で入力してください。");
		}
		reviewMapper.insertReview(reviewDto);
	}

	@Transactional
	public void updateReview(Long userId, ReviewDto reviewDto) {
		ReviewDto existing = reviewMapper.findById(reviewDto.getReviewId());
		if (existing == null || !existing.getUserId().equals(userId)) {
			throw new IllegalArgumentException("編集する権限がありません。");
		}
		reviewMapper.updateReview(reviewDto);
	}

	@Transactional
	public void deleteReview(Long reviewId, Long userId) {
		reviewMapper.deleteById(reviewId, userId);
	}

	@Transactional(readOnly = true)
	public List<ReviewDto> getReviewsByUserId(Long userId) {
		return reviewMapper.findByUserId(userId);
	}

	@Transactional(readOnly = true)
	public int getReviewCountByUserId(Long userId) {
		return reviewMapper.countByUserId(userId);
	}

	/** 管理者: 全レビュー照会 */
	@Transactional(readOnly = true)
	public List<ReviewDto> getAllReviewsForAdmin() {
		return reviewMapper.findAllForAdmin();
	}

	/** 管理者: レビュー削除 */
	@Transactional
	public void deleteReviewByAdmin(Long reviewId) {
		reviewMapper.deleteByIdAdmin(reviewId);
	}

	/** 管理者: フィルタ適用レビュー照会 */
	@Transactional(readOnly = true)
	public List<ReviewDto> findWithFilters(String keyword, Integer rating, String startDate, String endDate) {
		return reviewMapper.findWithFilters(keyword, rating, startDate, endDate);
	}
}
