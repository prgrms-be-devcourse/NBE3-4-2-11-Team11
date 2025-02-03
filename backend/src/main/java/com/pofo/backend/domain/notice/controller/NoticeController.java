package com.pofo.backend.domain.notice.controller;

import java.util.List;

import com.pofo.backend.common.response.ResponseMessage;
import com.pofo.backend.domain.notice.dto.reponse.NoticeCreateResponse;
import com.pofo.backend.domain.notice.dto.reponse.NoticeDetailResponse;
import com.pofo.backend.domain.notice.service.NoticeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/common/notices")
@RequiredArgsConstructor
public class NoticeController {

	private final NoticeService noticeService;

	@GetMapping("/{id}")
	public ResponseMessage<NoticeDetailResponse> getNoticeDetail(@PathVariable("id") Long id) {
		NoticeDetailResponse notice = this.noticeService.findById(id);
		return new ResponseMessage<>("공지사항 상세 조회가 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notice);
	}

	@GetMapping("")
	public ResponseMessage<List<NoticeDetailResponse>> getAllNotices() {
		List<NoticeDetailResponse> notices = this.noticeService.findAll();
		return new ResponseMessage<>("공지사항 조회가 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notices);
	}
}

