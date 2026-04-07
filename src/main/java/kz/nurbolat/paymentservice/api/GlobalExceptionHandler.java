package kz.nurbolat.paymentservice.api;

import kz.nurbolat.paymentservice.api.dto.ErrorResponse;
import kz.nurbolat.paymentservice.service.ConflictException;
import kz.nurbolat.paymentservice.service.InvalidOperationException;
import kz.nurbolat.paymentservice.service.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex,
                                                                                 HttpServletRequest request) {
        return org.springframework.http.ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage(), request.getRequestURI(), Instant.now()));
    }

    @ExceptionHandler(InvalidOperationException.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> handleInvalidOperation(InvalidOperationException ex,
                                                                                           HttpServletRequest request) {
        return org.springframework.http.ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_OPERATION", ex.getMessage(), request.getRequestURI(), Instant.now()));
    }

    @ExceptionHandler(ConflictException.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> handleConflict(ConflictException ex,
                                                                                 HttpServletRequest request) {
        return org.springframework.http.ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONFLICT", ex.getMessage(), request.getRequestURI(), Instant.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                                                    HttpServletRequest request) {
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toMessage)
                .collect(Collectors.joining(", "));
        return org.springframework.http.ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", details, request.getRequestURI(), Instant.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex,
                                                                                            HttpServletRequest request) {
        return org.springframework.http.ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", "Invalid request payload", request.getRequestURI(), Instant.now()));
    }

    private String toMessage(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
