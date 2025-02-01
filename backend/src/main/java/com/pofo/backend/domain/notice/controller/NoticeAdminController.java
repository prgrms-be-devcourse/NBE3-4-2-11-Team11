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
import com.pofo.backend.domain.notice.dto.NoticeRequestDto;
import com.pofo.backend.domain.notice.dto.NoticeResponseDto;
import com.pofo.backend.domain.notice.service.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NoticeAdminController {

	private final NoticeService noticeService;

	@PostMapping("/notice")
	public ResponseMessage<NoticeResponseDto> createNotice(@RequestBody NoticeRequestDto noticeRequestDto) {
		NoticeResponseDto notice = this.noticeService.create(noticeRequestDto);
		return new ResponseMessage<>("공지사항 생성이 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notice);
	}

	@PatchMapping("/notices/{id}")
	public ResponseMessage<NoticeResponseDto> updateNotice(@PathVariable("id") Long id,
		@RequestBody NoticeRequestDto noticeRequestDto) {
		NoticeResponseDto notice = this.noticeService.update(id, noticeRequestDto);
		return new ResponseMessage<>("공지사항 수정이 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notice);
	}

	@DeleteMapping("/notices/{id}")
	public ResponseMessage<NoticeResponseDto> deleteNotice(@PathVariable("id") Long id) {
		this.noticeService.delete(id);
		return new ResponseMessage<>("공지사항 삭제가 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), null);
	}
}