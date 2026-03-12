package com.blog.service;

import com.blog.dto.ReservationDto;
import com.blog.mapper.ReservationMapper;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationMapper reservationMapper;

    public ReservationService(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    @Transactional
    public void saveReservation(ReservationDto dto) {
        reservationMapper.insertReservation(dto);
    }
    
    // 유저별 총 예약 건수 반환
    public int getReservationCount(Long userId) {
        return reservationMapper.countReservationsByUserId(userId);
    }

    // 유저별 최근 예약 목록 5건 반환
    public List<ReservationDto> getRecentReservations(Long userId) {
        return reservationMapper.findRecentReservationsByUserId(userId);
    }
    
    // 유저별 '모든' 예약 목록 반환
    public List<ReservationDto> getAllReservations(Long userId) {
        return reservationMapper.findAllReservationsByUserId(userId);
    }

    // 관리자용: 모든 유저의 예약 목록 반환
    public List<ReservationDto> getAllReservationsForAdmin() {
        return reservationMapper.findAllWithUser();
    }

    // 주문 번호(orderId)로 특정 예약 상세 정보 가져오기
    public ReservationDto getReservationByOrderId(String orderId) {
        return reservationMapper.findByOrderId(orderId);
    }
}