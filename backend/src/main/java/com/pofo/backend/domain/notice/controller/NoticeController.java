package com.pofo.backend.domain.notice.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.notice.dto.reponse.NoticeDetailResponse;
import com.pofo.backend.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/common/notices")
@RequiredArgsConstructor
public class NoticeController {

	private final NoticeService noticeService;

	@GetMapping("/{id}")
	public ResponseEntity<RsData<NoticeDetailResponse>> getNoticeDetail(@PathVariable("id") Long id) {
		NoticeDetailResponse noticeDetailResponse = this.noticeService.findById(id);
		return ResponseEntity.ok(new RsData<>("200", "공지사항 상세 조회가 완료되었습니다.", noticeDetailResponse));
	}

	@GetMapping("")
	public ResponseEntity<RsData<List<NoticeDetailResponse>>> getAllNotices() {
		List<NoticeDetailResponse> noticeDetailResponses = this.noticeService.findAll();
        return ResponseEntity.ok(new RsData<>("200", "공지사항 조회가 완료되었습니다.", noticeDetailResponses));
	}
}

