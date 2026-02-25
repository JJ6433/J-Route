package com.blog.service;

import com.blog.dto.NoticeDto;
import com.blog.mapper.NoticeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Transactional(readOnly = true)
    public List<NoticeDto> getAllNotices() {
        return noticeMapper.findAll();
    }

    @Transactional(readOnly = true)
    public List<NoticeDto> getActiveNotices() {
        return noticeMapper.findActive();
    }

    @Transactional(readOnly = true)
    public NoticeDto getNoticeById(Long noticeId) {
        return noticeMapper.findById(noticeId);
    }

    @Transactional
    public void createNotice(NoticeDto notice) {
        noticeMapper.insert(notice);
    }

    @Transactional
    public void updateNotice(NoticeDto notice) {
        noticeMapper.update(notice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        noticeMapper.delete(noticeId);
    }
}
