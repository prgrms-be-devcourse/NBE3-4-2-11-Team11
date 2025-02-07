package com.pofo.backend.common.exception;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.inquiry.exception.InquiryException;
import com.pofo.backend.domain.notice.exception.NoticeException;
import com.pofo.backend.domain.reply.exception.ReplyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoticeException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public RsData<Object> handleNoticeException(NoticeException e) {
		return new RsData<>("404", e.getMessage());
	}

	@ExceptionHandler(InquiryException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public RsData<Object> handleInquiryException(InquiryException e) {
		return new RsData<>("404", e.getMessage());
	}

	@ExceptionHandler(ReplyException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public RsData<Object> handleReplyException(ReplyException e) {
		return new RsData<>("404", e.getMessage());
	}
}
