package com.pofo.backend.domain.notice.controller;

import java.util.List;

import com.pofo.backend.common.response.ResponseMessage;
import com.pofo.backend.domain.notice.dto.NoticeRequestDto;
import com.pofo.backend.domain.notice.dto.NoticeResponseDto;
import com.pofo.backend.domain.notice.entity.Notice;
import com.pofo.backend.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/admin/notice")
    public ResponseMessage<NoticeResponseDto> createNotice(@RequestBody NoticeRequestDto noticeRequestDto) {
        NoticeResponseDto notice = this.noticeService.create(noticeRequestDto);

        return new ResponseMessage<>("공지사항 생성이 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notice);
    }

    @PatchMapping("/admin/notice/{id}")
    public ResponseMessage<NoticeResponseDto> updateNotice(@PathVariable("id") Long id, @RequestBody NoticeRequestDto noticeRequestDto) {
        NoticeResponseDto notice = this.noticeService.update(id, noticeRequestDto);

        return new ResponseMessage<>("공지사항 수정이 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notice);
    }

    // @GetMapping("/common/notices")
    // public ResponseMessage<List<NoticeResponseDto>> getAllNotices() {
    //
    // }
}

