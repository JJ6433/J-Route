package com.blog.mapper;

import com.blog.dto.PlaceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 旅行先 MyBatis Mapper
 * リスト/詳細/フィルタ、管理者 CRUD
 */
@Mapper
public interface PlaceMapper {

	List<PlaceDto> findAll();

	/** 旅行先一覧・検索照会 */
	List<PlaceDto> findList(@Param("region") String region, @Param("category") String category,
			@Param("keyword") String keyword, @Param("sort") String sort);

	PlaceDto findById(Long placeId);

	void insertPlace(PlaceDto placeDto);

	int updatePlace(PlaceDto placeDto);

	int deleteById(Long placeId);

	/** 人気旅行先照会 */
	List<PlaceDto> findTopPlaces(@Param("limit") int limit);

	/** 旅行先総数照会 */
	int count();
}
