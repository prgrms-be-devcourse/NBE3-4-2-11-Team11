package com.pofo.backend.domain.reply.repository;

import com.pofo.backend.domain.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
}
