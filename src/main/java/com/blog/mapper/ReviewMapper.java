package com.blog.mapper;

import com.blog.dto.ReviewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * レビュー MyBatis Mapper
 * 旅行先別レビュー照会/登録/修正/削除、平均評価
 */
@Mapper
public interface ReviewMapper {

	List<ReviewDto> findByPlaceId(Long placeId);

	void insertReview(ReviewDto reviewDto);

	int updateReview(ReviewDto reviewDto);

	int deleteById(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

	/** 平均評価照会 */
	Double getAvgRatingByPlaceId(Long placeId);

	/** レビュー数照会 */
	int countByPlaceId(Long placeId);

	ReviewDto findById(Long reviewId);

	List<ReviewDto> findByUserId(Long userId);

	int countByUserId(Long userId);

	/** 管理者: 全レビュー照会 */
	List<ReviewDto> findAllForAdmin();

	List<ReviewDto> findWithFilters(@Param("keyword") String keyword, @Param("rating") Integer rating,
			@Param("startDate") String startDate, @Param("endDate") String endDate);

	/** 管理者: レビュー削除 */
	int deleteByIdAdmin(Long reviewId);
}
