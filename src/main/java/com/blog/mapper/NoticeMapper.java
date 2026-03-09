package com.blog.mapper;

import com.blog.dto.NoticeDto;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NoticeMapper {
        @Select("SELECT notice_id AS noticeId, title, content, is_active AS active, " +
                        "created_at AS createdAt, updated_at AS updatedAt FROM notices ORDER BY created_at DESC")
        List<NoticeDto> findAll();

        @Select("SELECT notice_id AS noticeId, title, content, is_active AS active, " +
                        "created_at AS createdAt, updated_at AS updatedAt FROM notices WHERE is_active = TRUE ORDER BY created_at DESC")
        List<NoticeDto> findActive();

        @Select("SELECT notice_id AS noticeId, title, content, is_active AS active, " +
                        "created_at AS createdAt, updated_at AS updatedAt FROM notices WHERE notice_id = #{noticeId}")
        NoticeDto findById(Long noticeId);

        @Insert("INSERT INTO notices (title, content, is_active) VALUES (#{title}, #{content}, #{active})")
        @Options(useGeneratedKeys = true, keyProperty = "noticeId")
        void insert(NoticeDto notice);

        @Update("UPDATE notices SET title = #{title}, content = #{content}, is_active = #{active} WHERE notice_id = #{noticeId}")
        void update(NoticeDto notice);

        @Delete("DELETE FROM notices WHERE notice_id = #{noticeId}")
        void delete(Long noticeId);
}
