package com.pofo.backend.domain.reply.service;

import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.inquiry.repository.InquiryRepository;
import com.pofo.backend.domain.reply.dto.request.ReplyCreateRequest;
import com.pofo.backend.domain.reply.dto.request.ReplyUpdateRequest;
import com.pofo.backend.domain.reply.dto.response.ReplyCreateResponse;
import com.pofo.backend.domain.reply.dto.response.ReplyDetailResponse;
import com.pofo.backend.domain.reply.dto.response.ReplyUpdateResponse;
import com.pofo.backend.domain.reply.entity.Reply;
import com.pofo.backend.domain.reply.exception.ReplyException;
import com.pofo.backend.domain.reply.repository.ReplyRepository;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public ReplyCreateResponse create(Long id, ReplyCreateRequest replyCreateRequest, Admin admin) {

        if (admin == null) {
            throw new ReplyException("관리자 정보가 유효하지 않습니다.");
        }

        Inquiry inquiry = this.inquiryRepository.findById(id)
                .orElseThrow(() -> new ReplyException("문의사항을 찾을 수 없습니다."));

        try {
            Reply reply = Reply.builder()
                    .admin(admin)
                    .inquiry(inquiry)
                    .content(replyCreateRequest.getContent())
                    .build();

            this.replyRepository.save(reply);
            inquiry.addResponse(); // 답변 등록으로 response 값 1로 변경
            return new ReplyCreateResponse(reply.getId());
        } catch (Exception e) {
            throw new ReplyException("답변 생성 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }

    @Transactional
    public ReplyUpdateResponse update(Long inquiryId, Long replyId, ReplyUpdateRequest replyUpdateRequest, Admin admin) {

        Reply reply = this.replyRepository.findByInquiryIdAndId(inquiryId, replyId)
                .orElseThrow(() -> new ReplyException("해당 답변을 찾을 수 없습니다."));

        if (!reply.getAdmin().equals(admin)) {
            throw new UnauthorizedActionException("답변을 수정할 권한이 없습니다.");
        }

        try {
            reply.update(replyUpdateRequest.getContent());
            return new ReplyUpdateResponse(reply.getId());
        } catch (Exception e) {
            throw new ReplyException("답변 수정 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }

    @Transactional
    public void delete(Long inquiryId, Long replyId, Admin admin) {

        Reply reply = this.replyRepository.findByInquiryIdAndId(inquiryId, replyId)
                .orElseThrow(() -> new ReplyException("해당 답변을 찾을 수 없습니다."));

        if (!reply.getAdmin().equals(admin)) {
            throw new UnauthorizedActionException("답변을 수정할 권한이 없습니다.");
        }

        try {
            this.replyRepository.delete(reply);
            Inquiry inquiry = this.inquiryRepository.findById(inquiryId)
                    .orElseThrow(() -> new ReplyException("문의사항을 찾을 수 없습니다."));
            inquiry.deleteResponse();
        } catch (Exception e) {
            throw new ReplyException("답변 삭제 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ReplyDetailResponse> findByInquiryId(Long inquiryId) {

        List<Reply> replies = this.replyRepository.findByInquiryId(inquiryId);
        return replies.stream()
                .map(reply -> new ReplyDetailResponse(reply.getId(), reply.getContent(), reply.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReplyDetailResponse findById(Long id) {

        Reply reply = this.replyRepository.findById(id)
                .orElseThrow(() -> new ReplyException("해당 답변을 찾을 수 없습니다."));

        return new ReplyDetailResponse(reply.getId(), reply.getContent(), reply.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public Long count() {
        return this.replyRepository.count();
    }
}
