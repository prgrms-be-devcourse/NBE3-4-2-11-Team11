package com.pofo.backend.common.jpa.entity;

import com.pofo.backend.domain.notice.dto.request.NoticeCreateRequest;
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

        for (int i = 1; i <= 5; i++) {
            NoticeCreateRequest noticeCreateRequest = new NoticeCreateRequest();
            noticeCreateRequest.setSubject("공지사항 테스트 " + i + "번");
            noticeCreateRequest.setContent("공지사항 테스트 " + i + "번 입니다.");
            noticeService.create(noticeCreateRequest);
        }
    }
}
