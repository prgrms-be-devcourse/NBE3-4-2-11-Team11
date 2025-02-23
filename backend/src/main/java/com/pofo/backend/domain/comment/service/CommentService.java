package com.pofo.backend.domain.comment.service;

import com.pofo.backend.domain.comment.dto.request.CommentCreateRequest;
import com.pofo.backend.domain.comment.dto.request.CommentUpdateRequest;
import com.pofo.backend.domain.comment.dto.response.CommentCreateResponse;
import com.pofo.backend.domain.comment.dto.response.CommentDetailResponse;
import com.pofo.backend.domain.comment.dto.response.CommentUpdateResponse;
import com.pofo.backend.domain.comment.entity.Comment;
import com.pofo.backend.domain.comment.exception.CommentException;
import com.pofo.backend.domain.comment.repository.CommentRepository;
import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.inquiry.exception.InquiryException;
import com.pofo.backend.domain.inquiry.repository.InquiryRepository;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import com.pofo.backend.domain.user.join.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public CommentUpdateResponse update(Long inquiryId, Long commentId, CommentUpdateRequest commentUpdateRequest, User user) {

        Comment comment = this.commentRepository.findByInquiryIdAndId(inquiryId, commentId)
                .orElseThrow(() -> new CommentException("해당 댓글을 찾을 수 없습니다."));

        if (!comment.getUser().equals(user)) {
            throw new CommentException("댓글을 수정할 권한이 없습니다.");
        }

        try {
            comment.update(commentUpdateRequest.getContent());
            return new CommentUpdateResponse(comment.getId());
        } catch (Exception e) {
            throw new CommentException("댓글 수정 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }

    @Transactional
    public void delete(Long inquiryId, Long commentId, User user) {

        Comment comment = this.commentRepository.findByInquiryIdAndId(inquiryId, commentId)
                .orElseThrow(() -> new CommentException("해당 댓글을 찾을 수 없습니다."));

        if (!comment.getUser().equals(user)) {
            throw new UnauthorizedActionException("댓글을 수정할 권한이 없습니다.");
        }

        try {
            this.commentRepository.delete(comment);
        } catch (Exception e) {
            throw new CommentException("댓글 삭제 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<CommentDetailResponse> findByInquiryId(Long inquiryId) {

        List<Comment> comments = this.commentRepository.findByInquiryId(inquiryId);
        return comments.stream()
                .map(comment -> new CommentDetailResponse(comment.getId(), comment.getContent(), comment.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentDetailResponse findById(Long id) {

        Comment comment = this.commentRepository.findById(id)
                .orElseThrow(() -> new CommentException("해당 댓글을 찾을 수 없습니다."));

        return new CommentDetailResponse(comment.getId(), comment.getContent(), comment.getCreatedAt());
    }

    @Transactional
    public Long count() {
        return this.commentRepository.count();
    }
}
