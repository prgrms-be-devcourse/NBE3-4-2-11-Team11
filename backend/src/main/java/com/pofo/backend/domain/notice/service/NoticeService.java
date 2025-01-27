package com.pofo.backend.domain.notice.service;

import com.pofo.backend.domain.notice.dto.NoticeRequestDto;
import com.pofo.backend.domain.notice.entity.Notice;
import com.pofo.backend.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Notice create(NoticeRequestDto noticeRequestDto) {

        Notice notice = Notice.builder()
                .subject(noticeRequestDto.getSubject())
                .content(noticeRequestDto.getContent())
                .build();

        this.noticeRepository.save(notice);
        return notice;
    }
}
