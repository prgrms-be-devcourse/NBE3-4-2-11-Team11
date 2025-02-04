package com.pofo.backend.common.exception;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.pofo.backend.domain.notice.exception.NoticeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResumeCreationException.class)
    public ResponseEntity<RsData<Object>> handleResumeCreationException(ResumeCreationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new RsData<>("400", e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<RsData<Object>> handleUnauthorizedException(UnauthorizedActionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new RsData<>("403", e.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<RsData<Object>> handleDatabaseException(DataAccessException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new RsData<>("500", "데이터베이스 오류가 발생했습니다."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RsData<Object>> handleUnexpectedException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new RsData<>("500", "서버 내부 오류가 발생했습니다."));
    }

	@ExceptionHandler(NoticeException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public RsData<Object> handleNoticeException(NoticeException e) {
		return new RsData<>("404", e.getMessage());
	}
}
