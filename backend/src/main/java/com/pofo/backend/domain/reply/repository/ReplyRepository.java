package com.pofo.backend.domain.reply.repository;

import com.pofo.backend.domain.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findByInquiryIdAndId(Long inquiryId, Long replyId);
    List<Reply> findByInquiryId(Long inquiryId);
    boolean existsByInquiryId(Long id);
    void deleteByInquiryId(Long inquiryId);
}
