package com.dev.order_service.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<String> handleExternalService(ExternalServiceException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeign(FeignException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("External service communication failure");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleGeneric(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}
