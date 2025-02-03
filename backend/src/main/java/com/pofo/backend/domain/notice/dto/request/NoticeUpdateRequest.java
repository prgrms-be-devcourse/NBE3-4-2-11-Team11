package com.pofo.backend.domain.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeUpdateRequest {
	
	@NotBlank(message = "제목은 필수 항목입니다.")
	private String subject;

	@NotBlank(message = "내용은 필수 항목입니다.")
	private String content;
}
