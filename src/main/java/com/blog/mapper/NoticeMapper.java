package com.blog.mapper;

import com.blog.dto.NoticeDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface NoticeMapper {
    List<NoticeDto> findAll();

    List<NoticeDto> findActive();

    NoticeDto findById(Long noticeId);

    void insert(NoticeDto notice);

    void update(NoticeDto notice);

    void delete(Long noticeId);
}
