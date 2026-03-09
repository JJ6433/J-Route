package com.blog.mapper;

import com.blog.dto.ReservationDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReservationMapper {
    
    // 기존의 예약 정보 저장
    void insertReservation(ReservationDto reservationDto);

    // 마이페이지용 1: 특정 유저의 총 예약 건수 조회
    int countReservationsByUserId(Long userId);
    List<ReservationDto> findRecentReservationsByUserId(Long userId);
    List<ReservationDto> findAllReservationsByUserId(Long userId);

    // 마이페이지용 2: 특정 유저의 최근 예약 내역 조회 (최신순)
    List<ReservationDto> findRecentReservationsByUserId(String userId);
    
    // 추가: 유저의 '모든' 예약 내역 조회
    List<ReservationDto> findAllReservationsByUserId(String userId);
}