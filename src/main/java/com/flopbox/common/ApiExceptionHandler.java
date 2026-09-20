package com.flopbox.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail notFound(NotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, exception.getMessage(), "resource-not-found");
    }

    @ExceptionHandler(ConflictException.class)
    ProblemDetail conflict(ConflictException exception) {
        return problem(HttpStatus.CONFLICT, exception.getMessage(), "conflict");
    }

    @ExceptionHandler({BadCredentialsException.class})
    ProblemDetail unauthorized(BadCredentialsException exception) {
        return problem(HttpStatus.UNAUTHORIZED, "Invalid username or password.", "invalid-credentials");
    }

    @ExceptionHandler(FtpGatewayException.class)
    ProblemDetail ftpFailure(FtpGatewayException exception) {
        return problem(HttpStatus.BAD_GATEWAY, exception.getMessage(), "ftp-gateway-error");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail invalidRequest(MethodArgumentNotValidException exception) {
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Request validation failed.", "validation-error");
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ProblemDetail uploadTooLarge(MaxUploadSizeExceededException exception) {
        return problem(HttpStatus.PAYLOAD_TOO_LARGE, "The uploaded file exceeds the configured limit.", "file-too-large");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail integrityFailure(DataIntegrityViolationException exception) {
        return problem(HttpStatus.CONFLICT, "The requested change conflicts with existing data.", "integrity-conflict");
    }

    private ProblemDetail problem(HttpStatus status, String message, String type) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, message);
        detail.setType(URI.create("https://flopbox.dev/problems/" + type));
        return detail;
    }
}
