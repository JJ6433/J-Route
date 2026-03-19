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
    
    // ユーザー別総予約数照会
    public int getReservationCount(Long userId) {
        return reservationMapper.countReservationsByUserId(userId);
    }

    // 最近予約リスト照会（ダッシュボード等）
    public List<ReservationDto> getRecentReservations(Long userId) {
        return reservationMapper.findRecentReservationsByUserId(userId);
    }
    
    // 全予約リスト照会
    public List<ReservationDto> getAllReservations(Long userId) {
        return reservationMapper.findAllReservationsByUserId(userId);
    }

    // 管理者: 全予約照会
    public List<ReservationDto> getAllReservationsForAdmin() {
        return reservationMapper.findAllWithUser();
    }

    // 予約単一照会
    public ReservationDto getReservationByOrderId(String orderId) {
        return reservationMapper.findByOrderId(orderId);
    }
}