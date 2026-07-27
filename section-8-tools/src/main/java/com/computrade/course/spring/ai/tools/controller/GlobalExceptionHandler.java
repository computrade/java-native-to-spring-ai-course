package com.computrade.course.spring.ai.tools.controller;

import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ToolExecutionException.class)
    public ResponseEntity<Map<String, Object>> handleToolExecutionException(ToolExecutionException ex) {
        // Intercepts the framework failure automatically and responds directly to the browser
        Map<String, Object> errorDetails = Map.of(
                "timestamp", new Date(),
                "status", HttpStatus.FORBIDDEN.value(),
                "error", "Custom Callback Security Restriction",
                "message", ex.getMessage()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }
}
