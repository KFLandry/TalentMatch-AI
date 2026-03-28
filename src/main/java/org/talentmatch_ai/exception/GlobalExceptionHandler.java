package org.talentmatch_ai.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .collect(
                       Collectors.toMap(
                               FieldError::getField,
                               DefaultMessageSourceResolvable::getDefaultMessage
                        )
                );

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message(ErrorCode.VALIDATION_ERROR.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .details(errorDetails)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ){
        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.NOT_FOUND)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(GithubException.class)
    public ResponseEntity<ErrorResponse> handleGithubException(
            GithubException ex,
            HttpServletRequest request
    ) {

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.GITHUB_IMPORT_ERROR)
                .message(ErrorCode.GITHUB_IMPORT_ERROR.getMessage())
                .timestamp(LocalDateTime.now())
                .details(Map.of("error", ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(ConsumerException.class)
    public ResponseEntity<ErrorResponse> handleConsumerException(
            ConsumerException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.AI_SERVICE_ERROR)
                .message(ErrorCode.AI_SERVICE_ERROR.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.INTERNAL_ERROR)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build(
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}


