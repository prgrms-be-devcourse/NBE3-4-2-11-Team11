package com.pofo.backend.domain.notice.controller;

import com.pofo.backend.common.response.ResponseMessage;
import com.pofo.backend.domain.notice.dto.NoticeRequestDto;
import com.pofo.backend.domain.notice.dto.NoticeResponseDto;
import com.pofo.backend.domain.notice.entity.Notice;
import com.pofo.backend.domain.notice.service.NoticeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/admin/notion")
    public ResponseMessage<Notice> createNotice(@RequestBody NoticeRequestDto noticeRequestDto) {
        Notice notice = this.noticeService.create(noticeRequestDto);
        return new ResponseMessage<>("공지사항 생성이 완료되었습니다.", String.valueOf(HttpStatus.OK.value()), notice);
    }
}

