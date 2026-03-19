package com.blog.service;

import com.blog.dto.PlaceDto;
import com.blog.mapper.PlaceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 旅行先サービス
 * リスト/詳細/フィルタ、人気リスト、管理者 CRUD
 */
@Service
public class PlaceService {

	private final PlaceMapper placeMapper;

	public PlaceService(PlaceMapper placeMapper) {
		this.placeMapper = placeMapper;
	}

	@Transactional(readOnly = true)
	public List<PlaceDto> getAllPlaces() {
		return placeMapper.findAll();
	}

	/** フィルター・ソート適用リスト (region, category, keyword, sort: latest|popular|rating) */
	@Transactional(readOnly = true)
	public List<PlaceDto> getList(String region, String category, String keyword, String sort) {
		if (sort == null || sort.isEmpty())
			sort = "latest";
		return placeMapper.findList(region, category, keyword, sort);
	}

	@Transactional(readOnly = true)
	public PlaceDto getById(Long placeId) {
		return placeMapper.findById(placeId);
	}

	/** メイン用人気旅行地TopN */
	@Transactional(readOnly = true)
	public List<PlaceDto> getTopPlaces(int limit) {
		return placeMapper.findTopPlaces(limit);
	}

	@Transactional
	public void savePlace(PlaceDto placeDto) {
		if (placeDto.getPlaceId() == null) {
			placeMapper.insertPlace(placeDto);
		} else {
			placeMapper.updatePlace(placeDto);
		}
	}

	@Transactional
	public void deletePlace(Long placeId) {
		placeMapper.deleteById(placeId);
	}

	@Transactional(readOnly = true)
	public int count() {
		return placeMapper.count();
	}
}
