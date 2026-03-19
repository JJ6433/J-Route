package com.blog.dto;

import lombok.Data;

@Data
public class ExchangeDto {
    private double jpyToKrw;       // 1円対KRW為替レート
    private double jpy100ToKrw;    // 100円対KRW為替レート
    private String lastUpdateTime; // 為替レート基準時間
}