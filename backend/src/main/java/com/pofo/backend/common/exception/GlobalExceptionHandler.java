package com.pofo.backend.common.exception;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(ResumeCreationException.class)
    public ResponseEntity<RsData<Object>> handleResumeCreationException(ResumeCreationException e) {
        RsData<Object> rsData = new RsData<>("500", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rsData);
    }

}
