package com.blog.mapper;

import com.blog.dto.TransportDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TransportMapper {
    // パス券リストを取得
    List<TransportDto> getAllPasses();
    
    // パス券詳細を取得
    TransportDto getPassById(Long id);
    
    // パス券を登録
    void insertPass(TransportDto pass);
    
    // パス券を修正
    void updatePass(TransportDto pass);
    
    // パス券を削除
    void deletePass(Long id);
}