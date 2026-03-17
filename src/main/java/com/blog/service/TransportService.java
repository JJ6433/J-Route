package com.blog.service;

import com.blog.dto.TransportDto;
import com.blog.mapper.TransportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportService {

    @Autowired
    private TransportMapper transportMapper;

    // パス券リストを取得
    public List<TransportDto> getAllPasses() {
        return transportMapper.getAllPasses();
    }

    // 1件取得
    public TransportDto getPassById(Long id) {
        return transportMapper.getPassById(id);
    }

    // 保存（登録・修正）
    public void savePass(TransportDto pass) {
        if (pass.getId() == null) {
            transportMapper.insertPass(pass);
        } else {
            transportMapper.updatePass(pass);
        }
    }

    // 削除
    public void deletePass(Long id) {
        transportMapper.deletePass(id);
    }
}