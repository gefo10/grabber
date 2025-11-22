package com.grabbler.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.grabbler.payloads.exceptions.ErrorCode;
import com.grabbler.payloads.exceptions.ErrorResponse;
import com.grabbler.payloads.exceptions.FieldError;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== Validation Errors (400) ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Validation failed for one or more fields",
                fieldErrors,
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_REQUEST.getCode(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String message = "Invalid JSON request body. Please check syntax and data types.";
        if (ex.getMostSpecificCause() != null) {
            message = "Invalid request: " + ex.getMostSpecificCause().getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_REQUEST.getCode(),
                message,
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // ==================== Authentication Errors (401) ====================

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                ErrorCode.AUTHENTICATION_FAILED.getCode(),
                "Authentication failed: " + ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.AUTHENTICATION_FAILED.getCode(),
                "Invalid email or password",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // ==================== Authorization Errors (403) ====================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.ACCESS_DENIED.getCode(),
                "You don't have permission to access this resource",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // ==================== Not Found Errors (404) ====================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.USER_NOT_FOUND.getCode(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                "The requested endpoint does not exist: " + ex.getRequestURL(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    // ==================== Conflict Errors (409) ====================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        String message = "Data integrity violation";
        String code = ErrorCode.RESOURCE_ALREADY_EXISTS.getCode();

        if (ex.getCause() instanceof ConstraintViolationException) {
            String constraintMessage = ex.getRootCause().getMessage();
            if (constraintMessage.contains("email")) {
                message = "Email address already exists";
                code = ErrorCode.DUPLICATE_EMAIL.getCode();
            }
        } else {
            message = "Resource already exists or constraint violation";
        }

        ErrorResponse errorResponse = new ErrorResponse(code, message, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
            ObjectOptimisticLockingFailureException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.BUSINESS_RULE_VIOLATION.getCode(),
                "The item was modified by another transaction. Please try again.",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // ==================== Business Logic Errors (422) ====================

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorResponse> handleAPIException(
            APIException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.BUSINESS_RULE_VIOLATION.getCode(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    // ==================== Server Errors (500) ====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        // Log the full exception for debugging
        ex.printStackTrace();

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
