package com.pofo.backend.domain.notice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pofo.backend.common.response.ResponseMessage;
import com.pofo.backend.domain.notice.dto.reponse.NoticeCreateResponse;
import com.pofo.backend.domain.notice.dto.reponse.NoticeDeleteResponse;
import com.pofo.backend.domain.notice.dto.reponse.NoticeUpdateResponse;
import com.pofo.backend.domain.notice.dto.request.NoticeCreateRequest;
import com.pofo.backend.domain.notice.dto.request.NoticeUpdateRequest;
import com.pofo.backend.domain.notice.service.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NoticeAdminController {

	private final NoticeService noticeService;

	@PostMapping("/notice")
	public ResponseMessage<NoticeCreateResponse> createNotice(@RequestBody NoticeCreateRequest noticeCreateRequest) {
		NoticeCreateResponse notice = this.noticeService.create(noticeCreateRequest);
		return new ResponseMessage<>("공지사항 생성이 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notice);
	}

	@PatchMapping("/notices/{id}")
	public ResponseMessage<NoticeUpdateResponse> updateNotice(@PathVariable("id") Long id,
		@RequestBody NoticeUpdateRequest noticeUpdateRequest) {
		NoticeUpdateResponse notice = this.noticeService.update(id, noticeUpdateRequest);
		return new ResponseMessage<>("공지사항 수정이 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notice);
	}

	@DeleteMapping("/notices/{id}")
	public ResponseMessage<NoticeDeleteResponse> deleteNotice(@PathVariable("id") Long id) {
		this.noticeService.delete(id);
		return new ResponseMessage<>("공지사항 삭제가 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), null);
	}
}