package com.blog.service;

import com.blog.dto.PlaceDto;
import com.blog.mapper.PlaceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 여행지 서비스
 * 목록/상세/필터, 인기 목록, 관리자 CRUD
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

	/** 필터·정렬 적용 목록 (region, category, keyword, sort: latest|popular|rating) */
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

	/** 메인/대시보드용 인기 여행지 상위 N건 */
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
