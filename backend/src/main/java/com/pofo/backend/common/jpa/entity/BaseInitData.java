package com.pofo.backend.common.jpa.entity;

import com.pofo.backend.domain.inquiry.dto.request.InquiryCreateRequest;
import com.pofo.backend.domain.inquiry.service.InquiryService;
import com.pofo.backend.domain.notice.dto.request.NoticeCreateRequest;
import com.pofo.backend.domain.notice.service.NoticeService;
import com.pofo.backend.domain.reply.dto.request.ReplyCreateRequest;
import com.pofo.backend.domain.reply.service.ReplyService;
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

    @Autowired
    private final NoticeService noticeService;

    @Autowired
    private final InquiryService inquiryService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.makeSampleNotices();
            self.makeSampleInquiry();
            self.makeSampleReply();
        };
    }

    @Transactional
    public void makeSampleNotices() throws IOException {
        if (noticeService.count() > 0) return;

        for (int i = 1; i <= 5; i++) {
            NoticeCreateRequest noticeCreateRequest = new NoticeCreateRequest("공지사항 테스트 " + i + "번", "공지사항 테스트 " + i + "번 입니다.");
            this.noticeService.create(noticeCreateRequest);
        }
    }

    @Transactional
    public void makeSampleInquiry() throws IOException {
        if (inquiryService.count() > 0) return;

        for (int i = 1; i <= 5; i++) {
            InquiryCreateRequest inquiryCreateRequest = new InquiryCreateRequest("문의사항 테스트 " + i + "번", "문의사항 테스트 " + i + "번 입니다.");
            this.inquiryService.create(inquiryCreateRequest);
        }
    }

    @Transactional
    public void makeSampleReply() throws IOException {
        if (replyService.count() > 0) return;

        for (Long i = 1L; i <= 5L; i++) {
            ReplyCreateRequest replyCreateRequest = new ReplyCreateRequest("답변 테스트 " + i + "번");
            this.replyService.create(i, replyCreateRequest);
        }
    }
}
