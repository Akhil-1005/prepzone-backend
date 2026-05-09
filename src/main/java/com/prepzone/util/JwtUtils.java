package com.prepzone.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.prepzone.authentication.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    
    @Value("${app.jwtSecret}")
    private String jwtSecret;
    
    @Value("${app.jwtExpirationMs:900000}")
    private long jwtExpirationMs;
    
    @Value("${app.jwtRefreshExpirationMs:604800000}")
    private long refreshTokenExpirationMs;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                "JWT secret must be at least 256 bits (32 characters). " +
                "Current length: " + keyBytes.length + " bytes. " +
                "Generate a strong secret using: openssl rand -base64 32"
            );
        }
        
        return Keys.hmacShaKeyFor(keyBytes);
    }
    

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        return Jwts.builder()
                .subject(userPrincipal.getEmail()) 
                .claim("userId", userPrincipal.getId().toString())
                .claim("username", userPrincipal.getUsername()) 
                .claim("role", userPrincipal.getUserRole().name())
                .claim("type", "ACCESS")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(jwtExpirationMs, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }
    
   
    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        return Jwts.builder()
                .subject(userPrincipal.getEmail()) 
                .claim("userId", userPrincipal.getId().toString())
                .claim("type", "REFRESH")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }
    
   
    public String generateRefreshTokenFromEmail(String email, UUID userId) {
        return Jwts.builder()
                .subject(email) // ✅ EMAIL as main identifier
                .claim("userId", userId.toString())
                .claim("type", "REFRESH")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }
    

    public String generateTokenFromEmail(String email) {
        return Jwts.builder()
                .subject(email) 
                .claim("type", "ACCESS")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(jwtExpirationMs, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }
    
    // ✅ UPDATED: Get EMAIL from token (not username)
    // RENAMED METHOD: getEmailFromJwtToken (was: getUserNameFromJwtToken)
    // WHY: Subject now contains email, so this returns email
    public String getEmailFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject(); // ✅ Returns EMAIL (from subject)
    }
    
    // ✅ NEW: Get USERNAME from token claims
    // WHY: Username is now stored as a claim, not subject
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class); // ✅ Get username from claims
    }
    

    public UUID getUserIdFromJwtToken(String token) {
        String userIdStr = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", String.class);
        return UUID.fromString(userIdStr);
    }
    
    // ✅ UNCHANGED: Get role from token
    public String getRoleFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
    
    // ✅ UNCHANGED: Get token type
    public String getTokenType(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("type", String.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    // ✅ UNCHANGED: Check if refresh token
    public boolean isRefreshToken(String token) {
        try {
            String tokenType = getTokenType(token);
            return "REFRESH".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }
    
    // ✅ UNCHANGED: Validate JWT token
    public boolean validateJwtToken(String authToken) {
        try {
          	Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();
          	System.out.println("refresh token expiration"+claims.getExpiration());
            return true;
            
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (JwtException e) {
            logger.error("JWT error: {}", e.getMessage());
        }
        
        return false;
    }
    
    // ✅ UNCHANGED: Get all claims
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}