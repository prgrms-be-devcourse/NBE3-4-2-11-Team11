package com.pofo.backend.domain.comment.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.CustomUserDetails;
import com.pofo.backend.domain.comment.dto.request.CommentCreateRequest;
import com.pofo.backend.domain.comment.dto.response.CommentCreateResponse;
import com.pofo.backend.domain.comment.service.CommentService;
import com.pofo.backend.domain.user.join.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/inquiries/{inquiryId}/comment")
    public ResponseEntity<RsData<CommentCreateResponse>> reply(@PathVariable Long inquiryId, @RequestBody CommentCreateRequest commentCreateRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        CommentCreateResponse commentCreateResponse = this.commentService.create(inquiryId, commentCreateRequest, user);
        return ResponseEntity.ok(new RsData<>("200", "댓글 생성이 완료되었습니다.", commentCreateResponse));
    }
}
