package com.blog.mapper;

import com.blog.dto.TransportDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TransportMapper {
    // パス券リスト取得
    List<TransportDto> getAllPasses();
    
    // パス券詳細取得
    TransportDto getPassById(Long id);
    
    // パス券登録
    void insertPass(TransportDto pass);
    
    // パス券修正
    void updatePass(TransportDto pass);
    
    // パス券削除
    void deletePass(Long id);
}