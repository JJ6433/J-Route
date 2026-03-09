package com.blog.mapper;

import com.blog.dto.PlaceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 여행지 MyBatis Mapper
 * 목록/상세/필터, 관리자 CRUD
 */
@Mapper
public interface PlaceMapper {

	List<PlaceDto> findAll();

	/** 지역·카테고리 필터, 키워드 검색, 정렬(최신/인기/별점) */
	List<PlaceDto> findList(@Param("region") String region, @Param("category") String category,
			@Param("keyword") String keyword, @Param("sort") String sort);

	PlaceDto findById(Long placeId);

	void insertPlace(PlaceDto placeDto);

	int updatePlace(PlaceDto placeDto);

	int deleteById(Long placeId);

	/** 인기 여행지 (리뷰 수 또는 별점 기준, 상위 N건 - 대시보드/메인용) */
	List<PlaceDto> findTopPlaces(@Param("limit") int limit);

	/** 여행지 총 개수 (대시보드) */
	int count();
}
