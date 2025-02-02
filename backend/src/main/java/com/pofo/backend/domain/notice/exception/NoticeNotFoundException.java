package com.pofo.backend.domain.notice.exception;

public class NoticeNotFoundException extends RuntimeException {
	public NoticeNotFoundException(String message) {
		super(message);
	}
}
