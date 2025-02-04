package com.pofo.backend.domain.inquiry.service;

import com.pofo.backend.domain.inquiry.dto.reponse.InquiryCreateResponse;
import com.pofo.backend.domain.inquiry.dto.request.InquiryCreateRequest;
import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.inquiry.exception.InquiryException;
import com.pofo.backend.domain.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    public InquiryCreateResponse create(InquiryCreateRequest inquiryCreateRequest) {

//        if (user == null) {
//            throw new InquiryException("사용자 정보가 유효하지 않습니다.");
//        }
        try {
            Inquiry inquiry = Inquiry.builder()
//                    .user(user)
                    .subject(inquiryCreateRequest.getSubject())
                    .content(inquiryCreateRequest.getContent())
                    .build();

            inquiryRepository.save(inquiry);
            return new InquiryCreateResponse(inquiry.getId());
        } catch (Exception e) {
            throw new InquiryException("문의사항 생성 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }
}
