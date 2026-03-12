package com.blog.mapper;

import com.blog.dto.ReservationDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReservationMapper {
    
    // 예약 정보 저장
    void insertReservation(ReservationDto reservationDto);

    // 유저별 예약 정보 조회
    int countReservationsByUserId(Long userId);
    List<ReservationDto> findRecentReservationsByUserId(Long userId);
    List<ReservationDto> findAllReservationsByUserId(Long userId);

    // 관리자용: 모든 예약 내역 조회 (닉네임 포함)
    List<ReservationDto> findAllWithUser();

    // 주문번호(orderId)로 특정 예약 조회
    ReservationDto findByOrderId(String orderId);
}