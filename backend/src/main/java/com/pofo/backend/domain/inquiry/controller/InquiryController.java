package com.pofo.backend.domain.inquiry.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.inquiry.dto.reponse.InquiryCreateResponse;
import com.pofo.backend.domain.inquiry.dto.request.InquiryCreateRequest;
import com.pofo.backend.domain.inquiry.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("/user/inquiry")
    public ResponseEntity<RsData<InquiryCreateResponse>> createInquiry(@Valid @RequestBody InquiryCreateRequest inquiryCreateRequest) {
        InquiryCreateResponse inquiryCreateResponse = this.inquiryService.create(inquiryCreateRequest);
        return ResponseEntity.ok(new RsData<>("200", "문의사항 생성이 완료되었습니다.", inquiryCreateResponse));
    }
}
