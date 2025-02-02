package com.pofo.backend.common.exception;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.resume.exception.ResumeCreationException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(ResumeCreationException.class)
    public RsData<Object> handleResumeCreationException(ResumeCreationException e) {
        return new RsData<>("500", e.getMessage());
    }

}
