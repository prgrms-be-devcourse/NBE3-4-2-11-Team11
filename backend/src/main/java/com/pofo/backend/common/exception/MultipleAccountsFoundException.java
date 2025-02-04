package com.pofo.backend.common.exception;

import com.pofo.backend.common.rsData.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MultipleAccountsFoundException extends RuntimeException {
    public MultipleAccountsFoundException(String message) {
        super(message);
    }
}

@RestControllerAdvice
class MultipleAccountsFoundExceptionAdvice {

    @ExceptionHandler(MultipleAccountsFoundException.class)
    public ResponseEntity<RsData<String>> handleMultipleAccountsFoundException(
            MultipleAccountsFoundException e) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>("400",e.getMessage(),null));
    }
}