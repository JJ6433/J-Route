package com.blog.service;

import com.blog.dto.ReviewDto;
import com.blog.mapper.ReviewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 리뷰 서비스
 * 여행지별 리뷰 조회/등록/수정/삭제, 평균 별점
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

	/** 관리자: 모든 리뷰 조회 */
	@Transactional(readOnly = true)
	public List<ReviewDto> getAllReviewsForAdmin() {
		return reviewMapper.findAllForAdmin();
	}

	/** 관리자: 리뷰 삭제 */
	@Transactional
	public void deleteReviewByAdmin(Long reviewId) {
		reviewMapper.deleteByIdAdmin(reviewId);
	}

	/** 관리자: 필터링된 리뷰 목록 조회 */
	@Transactional(readOnly = true)
	public List<ReviewDto> findWithFilters(String keyword, Integer rating, String startDate, String endDate) {
		return reviewMapper.findWithFilters(keyword, rating, startDate, endDate);
	}
}
