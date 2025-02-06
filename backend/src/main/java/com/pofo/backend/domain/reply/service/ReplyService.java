package com.pofo.backend.domain.reply.service;

import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.inquiry.repository.InquiryRepository;
import com.pofo.backend.domain.reply.dto.request.ReplyCreateRequest;
import com.pofo.backend.domain.reply.dto.response.ReplyCreateResponse;
import com.pofo.backend.domain.reply.entity.Reply;
import com.pofo.backend.domain.reply.exception.ReplyException;
import com.pofo.backend.domain.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public ReplyCreateResponse create(Long id, ReplyCreateRequest replyCreateRequest) {

//        if (admin == null) {
//            throw new ReplyException("관리자 정보가 유효하지 않습니다.");
//        }

        Inquiry inquiry = this.inquiryRepository.findById(id)
                .orElseThrow(() -> new ReplyException("문의사항을 찾을 수 없습니다."));

        try {
            Reply reply = Reply.builder()
//                    .admin(admin)
                    .inquiry(inquiry)
                    .content(replyCreateRequest.getContent())
                    .build();

            this.replyRepository.save(reply);
            return new ReplyCreateResponse(reply.getId());
        } catch (Exception e) {
            throw new ReplyException("문의사항 답변 생성 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }
}
