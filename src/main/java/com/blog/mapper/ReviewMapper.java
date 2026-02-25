package com.blog.mapper;

import com.blog.dto.ReviewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 리뷰 MyBatis Mapper
 * 여행지별 리뷰 조회/등록/수정/삭제, 평균 별점
 */
@Mapper
public interface ReviewMapper {

	List<ReviewDto> findByPlaceId(Long placeId);

	void insertReview(ReviewDto reviewDto);

	int updateReview(ReviewDto reviewDto);

	int deleteById(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

	/** 여행지 평균 별점 */
	Double getAvgRatingByPlaceId(Long placeId);

	/** 여행지 리뷰 개수 */
	int countByPlaceId(Long placeId);

	ReviewDto findById(Long reviewId);

	List<ReviewDto> findByUserId(Long userId);

	int countByUserId(Long userId);

	/** 관리자용 모든 리뷰 조회 */
	List<ReviewDto> findAllForAdmin();

	List<ReviewDto> findWithFilters(@Param("keyword") String keyword, @Param("rating") Integer rating,
			@Param("startDate") String startDate, @Param("endDate") String endDate);

	/** 관리자 전용 리뷰 삭제 */
	int deleteByIdAdmin(Long reviewId);
}
