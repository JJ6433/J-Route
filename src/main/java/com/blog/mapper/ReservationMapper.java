package com.blog.mapper;

import com.blog.dto.ReservationDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReservationMapper {
    
    // 予約保存
    void insertReservation(ReservationDto reservationDto);

    // ユーザー別予約照会
    int countReservationsByUserId(Long userId);
    List<ReservationDto> findRecentReservationsByUserId(Long userId);
    List<ReservationDto> findAllReservationsByUserId(Long userId);

    // 管理者: 全予約照会
    List<ReservationDto> findAllWithUser();

    // 予約照会
    ReservationDto findByOrderId(String orderId);
}