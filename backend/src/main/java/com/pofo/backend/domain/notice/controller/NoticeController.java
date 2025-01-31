package com.pofo.backend.domain.notice.controller;

import java.util.List;

import com.pofo.backend.common.response.ResponseMessage;
import com.pofo.backend.domain.notice.dto.NoticeRequestDto;
import com.pofo.backend.domain.notice.dto.NoticeResponseDto;
import com.pofo.backend.domain.notice.service.NoticeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
// TODO : 권한 설정
public class NoticeController {

	private final NoticeService noticeService;

	@PostMapping("/admin/notice")
	public ResponseMessage<NoticeResponseDto> createNotice(@RequestBody NoticeRequestDto noticeRequestDto) {
		NoticeResponseDto notice = this.noticeService.create(noticeRequestDto);
		return new ResponseMessage<>("공지사항 생성이 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notice);
	}

	@PatchMapping("/admin/notices/{id}")
	public ResponseMessage<NoticeResponseDto> updateNotice(@PathVariable("id") Long id,
		@RequestBody NoticeRequestDto noticeRequestDto) {
		NoticeResponseDto notice = this.noticeService.update(id, noticeRequestDto);
		return new ResponseMessage<>("공지사항 수정이 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notice);
	}

	@DeleteMapping("/admin/notices/{id}")
	public ResponseMessage<NoticeResponseDto> deleteNotice(@PathVariable("id") Long id) {
		this.noticeService.delete(id);
		return new ResponseMessage<>("공지사항 삭제가 완료되었습니다.", String.valueOf(HttpStatus.OK), null);
	}

	@GetMapping("/common/notices/{id}")
	public ResponseMessage<NoticeResponseDto> getNoticeDetail(@PathVariable("id") Long id) {
		NoticeResponseDto notice = this.noticeService.findById(id);
		return new ResponseMessage<>("공지사항 상세 조회가 완료되었습니다.", String.valueOf(HttpStatus.OK), notice);
	}

	@GetMapping("/common/notices")
	public ResponseMessage<List<NoticeResponseDto>> getAllNotices() {
        List<NoticeResponseDto> notices = this.noticeService.findAll();
        return new ResponseMessage<>("공지사항 조회가 완료되었습니다.", String.valueOf(HttpStatus.OK), notices);
	}
}

