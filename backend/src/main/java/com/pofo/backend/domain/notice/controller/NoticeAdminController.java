package com.pofo.backend.domain.notice.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.AdminDetails;
import com.pofo.backend.common.security.AdminDetailsService;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.notice.dto.reponse.NoticeCreateResponse;
import com.pofo.backend.domain.notice.dto.reponse.NoticeUpdateResponse;
import com.pofo.backend.domain.notice.dto.request.NoticeCreateRequest;
import com.pofo.backend.domain.notice.dto.request.NoticeUpdateRequest;
import com.pofo.backend.domain.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeService noticeService;

    @PostMapping("/notice")
    public ResponseEntity<RsData<NoticeCreateResponse>> createNotice(@Valid @RequestBody NoticeCreateRequest noticeCreateRequest, @AuthenticationPrincipal AdminDetails adminDetails) {
        Admin admin = adminDetails.getAdmin();
        NoticeCreateResponse noticeCreateResponse = this.noticeService.create(noticeCreateRequest, admin);
        return ResponseEntity.ok(new RsData<>("200", "공지사항 생성이 완료되었습니다.", noticeCreateResponse));
    }

    @PatchMapping("/notices/{id}")
    public ResponseEntity<RsData<NoticeUpdateResponse>> updateNotice(@PathVariable("id") Long id, @Valid @RequestBody NoticeUpdateRequest noticeUpdateRequest, @AuthenticationPrincipal AdminDetails adminDetails) {
        Admin admin = adminDetails.getAdmin();
        NoticeUpdateResponse noticeUpdateResponse = this.noticeService.update(id, noticeUpdateRequest, admin);
        return ResponseEntity.ok(new RsData<>("200", "공지사항 수정이 완료되었습니다.", noticeUpdateResponse));
    }

    @DeleteMapping("/notices/{id}")
    public ResponseEntity<RsData<Void>> deleteNotice(@PathVariable("id") Long id, @AuthenticationPrincipal AdminDetails adminDetails) {
        Admin admin = adminDetails.getAdmin();
        noticeService.delete(id, admin);
        return ResponseEntity.ok(new RsData<>("200", "공지사항 삭제가 완료되었습니다."));
    }
}