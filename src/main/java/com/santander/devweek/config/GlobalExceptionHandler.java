package com.santander.devweek.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField, 
                FieldError::getDefaultMessage, 
                (existing, replacement) -> existing
            ));
        
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("errors", fieldErrors);
        
        // Log para console
        System.err.println("Validation Errors: " + fieldErrors);
        
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeExceptions(RuntimeException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("message", ex.getMessage());
        
        // Log para console
        System.err.println("Runtime Exception: " + ex.getMessage());
        ex.printStackTrace();
        
        return ResponseEntity.badRequest().body(error);
    }
}