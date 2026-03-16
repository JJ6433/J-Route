package com.blog.dto;

import lombok.Data;

@Data
public class ExchangeDto {
    private double jpyToKrw;       // 1엔당 원화 환율 (예: 8.95)
    private double jpy100ToKrw;    // 100엔당 원화 환율 (예: 895.00 - 한국인에게 가장 익숙한 표기법!)
    private String lastUpdateTime; // 환율 기준 시간
}