package com.pofo.backend.domain.comment.repository;

import com.pofo.backend.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByInquiryIdAndId(Long inquiryId, Long id);
    Optional<Comment> findByInquiryId(Long inquiryId);
    List<Comment> findAllByOrderByCreatedAtDesc();
}
