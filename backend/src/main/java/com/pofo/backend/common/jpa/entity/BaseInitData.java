package com.pofo.backend.common.jpa.entity;

import com.pofo.backend.domain.notice.dto.NoticeRequestDto;
import com.pofo.backend.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final NoticeService noticeService;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.makeSampleMenus();
        };
    }

    @Transactional
    public void makeSampleMenus() throws IOException {
        if (noticeService.count() > 0) return;
        NoticeRequestDto noticeRequestDto = new NoticeRequestDto();
        noticeRequestDto.setId(1L);
        noticeRequestDto.setSubject("공지사항 테스트");
        noticeRequestDto.setContent("공지사항 테스트입니다.");
        noticeService.create(noticeRequestDto);
    }
}
