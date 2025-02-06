package com.pofo.backend.domain.reply.repository;

import com.pofo.backend.domain.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findByIdAndInquiryId(Long replyId, Long inquiryId);

    boolean existsByInquiryId(Long id);
}
