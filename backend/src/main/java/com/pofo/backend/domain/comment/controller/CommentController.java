package com.pofo.backend.domain.comment.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.CustomUserDetails;
import com.pofo.backend.domain.comment.dto.request.CommentCreateRequest;
import com.pofo.backend.domain.comment.dto.request.CommentUpdateRequest;
import com.pofo.backend.domain.comment.dto.response.CommentCreateResponse;
import com.pofo.backend.domain.comment.dto.response.CommentUpdateResponse;
import com.pofo.backend.domain.comment.service.CommentService;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/inquiries/{inquiryId}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("")
    public ResponseEntity<RsData<CommentCreateResponse>> createComment(@PathVariable Long inquiryId, @Valid @RequestBody CommentCreateRequest commentCreateRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        CommentCreateResponse commentCreateResponse = this.commentService.create(inquiryId, commentCreateRequest, user);
        return ResponseEntity.ok(new RsData<>("200", "댓글 생성이 완료되었습니다.", commentCreateResponse));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<RsData<CommentUpdateResponse>> updateComment(@PathVariable Long inquiryId, @PathVariable Long commentId, @Valid @RequestBody CommentUpdateRequest commentUpdateRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        CommentUpdateResponse commentUpdateResponse = this.commentService.update(inquiryId, commentId, commentUpdateRequest, user);
        return ResponseEntity.ok(new RsData<>("200", "댓글 수정이 완료되었습니다.", commentUpdateResponse));
    }


}
