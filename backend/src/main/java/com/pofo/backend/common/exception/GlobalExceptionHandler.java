package com.pofo.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.notice.exception.NoticeNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoticeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public RsData<Object> handleNoticeNotFoundException(NoticeNotFoundException e) {
		return new RsData<>("404", e.getMessage());
	}
}
