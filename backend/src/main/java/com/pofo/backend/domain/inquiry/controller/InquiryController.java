package com.pofo.backend.domain.inquiry.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.CustomUserDetails;
import com.pofo.backend.domain.inquiry.dto.response.InquiryCreateResponse;
import com.pofo.backend.domain.inquiry.dto.response.InquiryDetailResponse;
import com.pofo.backend.domain.inquiry.dto.response.InquiryUpdateResponse;
import com.pofo.backend.domain.inquiry.dto.request.InquiryCreateRequest;
import com.pofo.backend.domain.inquiry.dto.request.InquiryUpdateRequest;
import com.pofo.backend.domain.inquiry.service.InquiryService;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("/user/inquiry")
    public ResponseEntity<RsData<InquiryCreateResponse>> createInquiry(@Valid @RequestBody InquiryCreateRequest inquiryCreateRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        InquiryCreateResponse inquiryCreateResponse = this.inquiryService.create(inquiryCreateRequest, user);
        return ResponseEntity.ok(new RsData<>("200", "문의사항 생성이 완료되었습니다.", inquiryCreateResponse));
    }

    @PatchMapping("/user/inquiries/{id}")
    public ResponseEntity<RsData<InquiryUpdateResponse>> updateInquiry(@PathVariable Long id, @Valid @RequestBody InquiryUpdateRequest inquiryUpdateRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        InquiryUpdateResponse inquiryUpdateResponse = this.inquiryService.update(id, inquiryUpdateRequest, user);
        return ResponseEntity.ok(new RsData<>("200", "문의사항 수정이 완료되었습니다.", inquiryUpdateResponse));
    }

    @DeleteMapping("/user/inquiries/{id}")
    public ResponseEntity<RsData<Void>> deleteInquiry(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        this.inquiryService.delete(id, user);
        return ResponseEntity.ok(new RsData<>("200", "문의사항 삭제가 완료되었습니다."));
    }

    @GetMapping("/common/inquiries/{id}")
    public ResponseEntity<RsData<InquiryDetailResponse>> getInquiryDetail(@PathVariable("id") Long id) {
        InquiryDetailResponse inquiryDetailResponse = this.inquiryService.findById(id);
        return ResponseEntity.ok(new RsData<>("200", "문의사항 상세 조회가 완료되었습니다.", inquiryDetailResponse));
    }

    @GetMapping("/common/inquiries")
    public ResponseEntity<RsData<List<InquiryDetailResponse>>> getAllInquiries() {
        List<InquiryDetailResponse> inquiryDetailResponses = this.inquiryService.findAll();
        return ResponseEntity.ok(new RsData<>("200", "문의사항 조회가 완료되었습니다.", inquiryDetailResponses));
    }
}
