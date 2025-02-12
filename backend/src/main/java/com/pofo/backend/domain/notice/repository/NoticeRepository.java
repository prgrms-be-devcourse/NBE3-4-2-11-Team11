package com.pofo.backend.domain.notice.repository;

import java.util.List;

import com.pofo.backend.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	List<Notice> findAllByOrderByCreatedAtDesc(); // 최신순으로 정렬
}
