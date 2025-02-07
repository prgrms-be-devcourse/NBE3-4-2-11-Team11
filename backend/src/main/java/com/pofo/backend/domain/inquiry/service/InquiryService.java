package com.pofo.backend.domain.inquiry.service;

import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.inquiry.dto.reponse.InquiryCreateResponse;
import com.pofo.backend.domain.inquiry.dto.reponse.InquiryDetailResponse;
import com.pofo.backend.domain.inquiry.dto.reponse.InquiryUpdateResponse;
import com.pofo.backend.domain.inquiry.dto.request.InquiryCreateRequest;
import com.pofo.backend.domain.inquiry.dto.request.InquiryUpdateRequest;
import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.inquiry.exception.InquiryException;
import com.pofo.backend.domain.inquiry.repository.InquiryRepository;
import com.pofo.backend.domain.reply.entity.Reply;
import com.pofo.backend.domain.reply.repository.ReplyRepository;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import com.pofo.backend.domain.user.join.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    private final ReplyRepository replyRepository;

    @Transactional
    public InquiryCreateResponse create(InquiryCreateRequest inquiryCreateRequest, User user) {

        if (user == null) {
            throw new InquiryException("사용자 정보가 유효하지 않습니다.");
        }

        try {
            Inquiry inquiry = Inquiry.builder()
                    .user(user)
                    .subject(inquiryCreateRequest.getSubject())
                    .content(inquiryCreateRequest.getContent())
                    .build();

            this.inquiryRepository.save(inquiry);
            return new InquiryCreateResponse(inquiry.getId());
        } catch (Exception e) {
            throw new InquiryException("문의사항 생성 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }

    @Transactional
    public InquiryUpdateResponse update(Long id, InquiryUpdateRequest updateRequest, User user) {

        Inquiry inquiry = this.inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryException("해당 문의사항을 찾을 수 없습니다."));

        if (!inquiry.getUser().equals(user)) {
            throw new UnauthorizedActionException("문의사항을 수정할 권한이 없습니다.");
        }

        try {
            inquiry.update(updateRequest.getSubject(), updateRequest.getContent());
            return new InquiryUpdateResponse(inquiry.getId());
        } catch (Exception e) {
            throw new InquiryException("문의사항 수정 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }

    @Transactional
    public void delete(Long id, User user, Admin admin) {

        Inquiry inquiry = this.inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryException("해당 문의사항을 찾을 수 없습니다."));


        if ((user != null && inquiry.getUser().equals(user)) || admin != null) {
            try {
                this.inquiryRepository.delete(inquiry);
            } catch (Exception e) {
                throw new InquiryException("문의사항 삭제 중 오류가 발생했습니다. 원인: " + e.getMessage());
            }
        } else {
            throw new UnauthorizedActionException("문의사항을 삭제할 권한이 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public InquiryDetailResponse findById(Long id) {

        Inquiry inquiry = this.inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryException("해당 문의사항을 찾을 수 없습니다."));

        // 문의글 조회 시 답변도 함께 조회
        Reply reply = this.replyRepository.findByInquiryId(id).orElse(null);

        return InquiryDetailResponse.from(inquiry, reply);
    }

    @Transactional(readOnly = true)
    public List<InquiryDetailResponse> findAll() {

        List<Inquiry> inquiries = this.inquiryRepository.findAllByOrderByCreatedAtDesc();
        return inquiries.stream()
                .map(inquiry -> {
                    Reply reply = this.replyRepository.findByInquiryId(inquiry.getId()).orElse(null);
                    return InquiryDetailResponse.from(inquiry, reply);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long count() {
        return this.inquiryRepository.count();
    }

}
