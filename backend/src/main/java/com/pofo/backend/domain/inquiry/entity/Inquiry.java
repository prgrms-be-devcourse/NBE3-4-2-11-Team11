package com.pofo.backend.domain.inquiry.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "inquiries")
public class Inquiry extends BaseTime {

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "user_id", nullable = false)
	// private User user;

	@Column(length = 100, nullable = false)
	private String subject;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@Column(nullable = false, columnDefinition = "TINYINT DEFAULT 0") // 문의사항 답변 상태
	private int response;

	public void update(String subject, String content) {
		this.subject = subject;
		this.content = content;
	}
}
