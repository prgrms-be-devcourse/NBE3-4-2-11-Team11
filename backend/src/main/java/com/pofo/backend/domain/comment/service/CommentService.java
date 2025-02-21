package com.pofo.backend.domain.comment.service;

import com.pofo.backend.domain.comment.dto.request.CommentCreateRequest;
import com.pofo.backend.domain.comment.dto.response.CommentCreateResponse;
import com.pofo.backend.domain.comment.entity.Comment;
import com.pofo.backend.domain.comment.exception.CommentException;
import com.pofo.backend.domain.comment.repository.CommentRepository;
import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.inquiry.exception.InquiryException;
import com.pofo.backend.domain.inquiry.repository.InquiryRepository;
import com.pofo.backend.domain.user.join.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public CommentCreateResponse create(Long id, CommentCreateRequest commentCreateRequest, User user) {

        if (user == null) {
            throw new CommentException("사용자 정보가 유효하지 않습니다.");
        }

        Inquiry inquiry = this.inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryException("문의사항을 찾을 수 없습니다."));

        try {
            Comment comment = Comment.builder()
                    .user(user)
                    .inquiry(inquiry)
                    .content(commentCreateRequest.getContent())
                    .build();

            this.commentRepository.save(comment);
            return new CommentCreateResponse(comment.getId());
        } catch (Exception e) {
            throw new CommentException("댓글 생성 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }
}
