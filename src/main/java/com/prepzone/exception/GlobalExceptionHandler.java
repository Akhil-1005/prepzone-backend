package com.prepzone.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * Global Exception Handler
 * 
 * PURPOSE: Catch ALL exceptions and return consistent error responses
 * 
 * WHY NEEDED:
 * - Proper HTTP status codes (404 for missing paths, not 401!) ✅
 * - User-friendly error messages
 * - Hide internal implementation details
 * - Consistent JSON error format
 * - Centralized error logging
 * 
 * HANDLES:
 * - 404 Not Found (path doesn't exist)
 * - 400 Bad Request (validation errors)
 * - 401 Unauthorized (authentication failed)
 * - 403 Forbidden (refresh token issues)
 * - 423 Locked (account locked)
 * - 500 Internal Server Error (unexpected errors)
 */
@RestControllerAdvice  // ✅ Makes this a global exception handler
@Slf4j
public class GlobalExceptionHandler {

    // ═══════════════════════════════════════════════════════════════
    //                      404 NOT FOUND
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Handle 404 - Path Not Found
     * 
     * ✅ FIXES YOUR ORIGINAL ISSUE!
     * 
     * BEFORE: GET /api/nonexistent → 401 Unauthorized ❌
     * AFTER:  GET /api/nonexistent → 404 Not Found ✅
     * 
     * WHEN TRIGGERED:
     * - User requests a path that doesn't exist
     * - Typo in URL
     * - Endpoint removed/renamed
     * 
     * EXAMPLE:
     * GET /api/this-does-not-exist
     * 
     * RESPONSE:
     * {
     *   "timestamp": "2024-11-02T15:30:45",
     *   "status": 404,
     *   "error": "Not Found",
     *   "message": "The requested path does not exist",
     *   "path": "/api/this-does-not-exist"
     * }
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(
            NoHandlerFoundException ex, WebRequest request) {
        
        log.warn("Path not found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", "The requested path does not exist");
        errorResponse.put("path", ex.getRequestURL());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // ═══════════════════════════════════════════════════════════════
    //                      400 BAD REQUEST
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Handle Validation Errors (400)
     * 
     * WHEN TRIGGERED:
     * - Request body validation fails
     * - @NotBlank, @Email, @Size, etc. validation fails
     * - Missing required fields
     * 
     * EXAMPLE REQUEST:
     * POST /api/auth/refresh-token
     * {
     *   "refreshToken": ""  ← Empty (violates @NotBlank)
     * }
     * 
     * RESPONSE:
     * {
     *   "timestamp": "2024-11-02T15:30:45",
     *   "status": 400,
     *   "error": "Validation Failed",
     *   "message": "Invalid input parameters",
     *   "validationErrors": {
     *     "refreshToken": "Refresh token is required"
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        // Extract field-specific errors
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        log.warn("Validation failed: {}", errors);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Failed");
        errorResponse.put("message", "Invalid input parameters");
        errorResponse.put("validationErrors", errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // ═══════════════════════════════════════════════════════════════
    //                      401 UNAUTHORIZED
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Handle Authentication Errors (401)
     * 
     * WHEN TRIGGERED:
     * - Invalid username/email or password
     * - User not found
     * 
     * SECURITY NOTE:
     * - Don't reveal if username exists or not
     * - Generic "Invalid username or password" message
     * - Prevents username enumeration attacks
     * 
     * EXAMPLE REQUEST:
     * POST /api/auth/signin
     * {
     *   "usernameOrEmail": "john@example.com",
     *   "password": "wrong_password"
     * }
     * 
     * RESPONSE:
     * {
     *   "timestamp": "2024-11-02T15:30:45",
     *   "status": 401,
     *   "error": "Unauthorized",
     *   "message": "Invalid username or password"
     * }
     */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            Exception ex, WebRequest request) {
        
        log.error("Authentication failed: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Invalid username or password");
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // ═══════════════════════════════════════════════════════════════
    //                      403 FORBIDDEN
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Handle Disabled Account (403)
     * 
     * WHEN TRIGGERED:
     * - User account is disabled by admin
     * - User violated terms of service
     * - Account pending verification
     * 
     * EXAMPLE:
     * User tries to login but account is disabled
     * 
     * RESPONSE:
     * {
     *   "timestamp": "2024-11-02T15:30:45",
     *   "status": 403,
     *   "error": "Forbidden",
     *   "message": "Account is disabled. Please contact support."
     * }
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabledException(
            DisabledException ex, WebRequest request) {
        
        log.error("Disabled account attempted login");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", "Account is disabled. Please contact support.");
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // ═══════════════════════════════════════════════════════════════
    //                      423 LOCKED
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Handle Locked Account (423)
     * 
     * WHEN TRIGGERED:
     * - Too many failed login attempts
     * - Suspicious activity detected
     * - Admin locked account
     * 
     * EXAMPLE:
     * User enters wrong password 5 times → account locked
     * 
     * RESPONSE:
     * {
     *   "timestamp": "2024-11-02T15:30:45",
     *   "status": 423,
     *   "error": "Locked",
     *   "message": "Account is locked. Please contact support."
     * }
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLockedException(
            LockedException ex, WebRequest request) {
        
        log.error("Locked account attempted login");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.LOCKED.value());
        errorResponse.put("error", "Locked");
        errorResponse.put("message", "Account is locked. Please contact support.");
        
        return ResponseEntity.status(HttpStatus.LOCKED).body(errorResponse);
    }

    // ═══════════════════════════════════════════════════════════════
    //                403 FORBIDDEN - REFRESH TOKEN
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Handle Token Refresh Errors (403)
     * 
     * WHEN TRIGGERED:
     * - Refresh token expired
     * - Refresh token revoked (user logged out)
     * - Refresh token not found in database
     * - Invalid refresh token format
     * 
     * EXAMPLE REQUEST:
     * POST /api/auth/refresh-token
     * {
     *   "refreshToken": "expired_or_revoked_token"
     * }
     * 
     * RESPONSE:
     * {
     *   "timestamp": "2024-11-02T15:30:45",
     *   "status": 403,
     *   "error": "Forbidden",
     *   "message": "Refresh token is invalid or expired. Please login again."
     * }
     * 
     * FRONTEND ACTION:
     * - Redirect user to login page
     * - Clear stored tokens
     * - Show "Session expired" message
     */
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<Map<String, Object>> handleTokenRefreshException(
            TokenRefreshException ex, WebRequest request) {
        
        log.error("Token refresh failed: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", "Refresh token is invalid or expired. Please login again.");
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // ═══════════════════════════════════════════════════════════════
    //                500 INTERNAL SERVER ERROR
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Handle Generic Errors (500)
     * 
     * WHEN TRIGGERED:
     * - Any unhandled exception
     * - Database connection errors
     * - Null pointer exceptions
     * - Unexpected errors
     * 
     * SECURITY:
     * - Don't expose internal error details to user
     * - Log full stack trace for debugging
     * - Return generic message to user
     * 
     * RESPONSE:
     * {
     *   "timestamp": "2024-11-02T15:30:45",
     *   "status": 500,
     *   "error": "Internal Server Error",
     *   "message": "An unexpected error occurred. Please try again later."
     * }
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        // ✅ Log full error details for debugging (server-side only)
        log.error("Unexpected error occurred", ex);
        
        // ✅ Return generic message (don't expose internals)
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred. Please try again later.");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}