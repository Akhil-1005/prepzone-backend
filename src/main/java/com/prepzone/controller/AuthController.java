package com.prepzone.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prepzone.authentication.UserPrincipal;
import com.prepzone.request.LoginRequest;
import com.prepzone.request.SignupRequest;
import com.prepzone.request.TokenRefreshRequest;
import com.prepzone.response.JwtResponse;
import com.prepzone.response.MessageResponse;
import com.prepzone.response.TokenRefreshResponse;
import com.prepzone.response.UserResponse;
import com.prepzone.serviceimpl.AuthServiceImpl;
import com.prepzone.util.HttpStatusCode;
import com.prepzone.util.ResponseModel;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

	private final AuthServiceImpl authService;
	private final HttpStatusCode httpStatusCode;

	// ═══════════════════════════════════════════════════════════════
	//                      SIGNIN (UPDATED)
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * User login endpoint
	 * 
	 * ✅ UPDATED: Now returns refresh token in addition to access token
	 * 
	 * REQUEST:
	 * POST /api/auth/signin
	 * {
	 *   "usernameOrEmail": "john@example.com",
	 *   "password": "password123"
	 * }
	 * 
	 * RESPONSE:
	 * {
	 *   "data": {
	 *     "token": "eyJhbGci... (access token)",
	 *     "refreshToken": "eyJzdWI... (refresh token)", ← ✅ NEW!
	 *     "type": "Bearer",
	 *     "id": "uuid",
	 *     "username": "johndoe",
	 *     "email": "john@example.com",
	 *     "role": "USER"
	 *   },
	 *   "statusCode": "200 OK",
	 *   "message": "User authenticated successfully"
	 * }
	 */
	@PostMapping("/signin")
	public ResponseEntity<ResponseModel<JwtResponse>> authenticateUser(
			 @RequestBody LoginRequest loginRequest) {
		
		ResponseModel<JwtResponse> jwtResponse = authService.authenticateUser(loginRequest);
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(jwtResponse.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(jwtResponse);
	}

	// ═══════════════════════════════════════════════════════════════
	//                      SIGNUP (UNCHANGED)
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * User registration endpoint
	 * 
	 * ✅ UNCHANGED - No modifications needed
	 */
	@PostMapping("/signup")
	public ResponseEntity<ResponseModel<UserResponse>> registerUser(
			 @RequestBody SignupRequest signUpRequest) {
		
		ResponseModel<UserResponse> userResponse = authService.registerUser(signUpRequest);
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(userResponse.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(userResponse);
	}
	
	// ═══════════════════════════════════════════════════════════════
	//                   REFRESH TOKEN (NEW ENDPOINT)
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Refresh access token endpoint
	 * 
	 * ✅ NEW ENDPOINT
	 * 
	 * PURPOSE: Get new access token without re-login
	 * 
	 * WHEN TO USE:
	 * - Access token expired (401 error)
	 * - Proactively refresh before expiration
	 * 
	 * REQUEST:
	 * POST /api/auth/refresh-token
	 * {
	 *   "refreshToken": "eyJzdWI..."
	 * }
	 * 
	 * SUCCESS RESPONSE (200 OK):
	 * {
	 *   "data": {
	 *     "accessToken": "NEW_eyJhbGci... (fresh 15 min)",
	 *     "refreshToken": "NEW_eyJzdWI... (fresh 7 days)",
	 *     "tokenType": "Bearer"
	 *   },
	 *   "statusCode": "200 OK",
	 *   "message": "Token refreshed successfully"
	 * }
	 * 
	 * ERROR RESPONSE (403 Forbidden):
	 * {
	 *   "statusCode": "403 FORBIDDEN",
	 *   "message": "Refresh token has expired. Please login again."
	 * }
	 * 
	 * WHAT IT DOES:
	 * 1. Validates refresh token
	 * 2. Checks if expired or revoked
	 * 3. Generates new access token
	 * 4. Generates new refresh token (token rotation)
	 * 5. Revokes old refresh token
	 * 6. Returns new tokens
	 * 
	 * @param request TokenRefreshRequest containing refresh token
	 * @return TokenRefreshResponse with new tokens
	 */
	@PostMapping("/refresh-token")
	public ResponseEntity<ResponseModel<TokenRefreshResponse>> refreshToken(
			 @RequestBody TokenRefreshRequest request) {
		
		log.info("Token refresh request received "+request);
		
		ResponseModel<TokenRefreshResponse> response = authService.refreshToken(request);
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	// ═══════════════════════════════════════════════════════════════
	//                 AVAILABILITY CHECKS (UNCHANGED)
	// ═══════════════════════════════════════════════════════════════
	
	@GetMapping("/check/username/{username}")
	public ResponseEntity<?> checkUsernameAvailability(@PathVariable String username) {
		boolean isAvailable = !authService.existsByEmail(username);
		return ResponseEntity.ok(
				new MessageResponse(isAvailable ? "Username is available" : "Username is already taken", isAvailable));
	}

	@GetMapping("/check/email/{email}")
	public ResponseEntity<?> checkEmailAvailability(@PathVariable String email) {
		boolean isAvailable = !authService.existsByEmail(email);
		return ResponseEntity
				.ok(new MessageResponse(isAvailable ? "Email is available" : "Email is already in use", isAvailable));
	}

	@GetMapping("/check/phone/{phoneNumber}")
	public ResponseEntity<?> checkPhoneAvailability(@PathVariable String phoneNumber) {
		boolean isAvailable = !authService.existsByPhoneNumber(phoneNumber);
		return ResponseEntity.ok(new MessageResponse(
				isAvailable ? "Phone number is available" : "Phone number is already in use", isAvailable));
	}

	// ═══════════════════════════════════════════════════════════════
	//                      PROFILE (UNCHANGED)
	// ═══════════════════════════════════════════════════════════════
	
	@GetMapping("/profile")
	public ResponseEntity<ResponseModel<UserResponse>> getUserProfile(
			@AuthenticationPrincipal UserPrincipal currentUser) {
		
	    ResponseModel<UserResponse> response = new ResponseModel<>();
	    try {
	        UserResponse userResponse = new UserResponse(
	                currentUser.getId(),
	                currentUser.getUsername(),
	                currentUser.getEmail(),
	                currentUser.getPhoneNumber(),
	                currentUser.getAddress(),
	                currentUser.getUserRole()
	        );

	        response.setData(userResponse);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("User profile retrieved successfully");

	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        log.error("Error retrieving user profile", e);

	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Error retrieving user profile");

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	// ═══════════════════════════════════════════════════════════════
	//                      LOGOUT (UPDATED)
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Logout endpoint (revokes refresh token)
	 * 
	 * ✅ UPDATED: Now revokes refresh token instead of just returning message
	 * 
	 * PURPOSE: Logout from current device
	 * 
	 * REQUEST:
	 * POST /api/auth/logout
	 * {
	 *   "refreshToken": "eyJzdWI..."
	 * }
	 * 
	 * RESPONSE:
	 * {
	 *   "statusCode": "200 OK",
	 *   "message": "Logged out successfully"
	 * }
	 * 
	 * WHAT IT DOES:
	 * 1. Finds refresh token in database
	 * 2. Sets revoked = true
	 * 3. Sets revokedAt = current timestamp
	 * 4. User is logged out from THIS device only
	 * 5. Other devices remain logged in
	 * 
	 * BEFORE vs AFTER:
	 * BEFORE: Just returned success message (token still valid!)
	 * AFTER: Actually revokes token (token becomes invalid)
	 * 
	 * @param request TokenRefreshRequest containing refresh token to revoke
	 * @return Success message
	 */
	@PostMapping("/logout")
	public ResponseEntity<ResponseModel<String>> logoutUser(
			@RequestBody TokenRefreshRequest request) {
		
		log.info("Logout request received");
		
		ResponseModel<String> response = authService.logout(request.getRefreshToken());
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}
	
	// ═══════════════════════════════════════════════════════════════
	//                 LOGOUT ALL DEVICES (NEW ENDPOINT)
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Logout from all devices endpoint
	 * 
	 * ✅ NEW ENDPOINT
	 * 
	 * PURPOSE: Logout from ALL devices at once
	 * 
	 * WHEN TO USE:
	 * - User clicks "Logout from all devices" button
	 * - User changes password (security measure)
	 * - Suspicious activity detected
	 * - User lost device
	 * 
	 * REQUEST:
	 * POST /api/auth/logout-all
	 * Authorization: Bearer {access_token}
	 * 
	 * RESPONSE:
	 * {
	 *   "statusCode": "200 OK",
	 *   "message": "Logged out from all devices successfully"
	 * }
	 * 
	 * WHAT IT DOES:
	 * 1. Gets current user from access token
	 * 2. Finds ALL refresh tokens for this user
	 * 3. Sets revoked = true for ALL tokens
	 * 4. User is logged out from ALL devices
	 * 5. Must login again on all devices
	 * 
	 * SECURITY BENEFIT:
	 * - If device is lost/stolen, can revoke all sessions remotely
	 * - Clean slate - all devices must re-authenticate
	 * 
	 * @param currentUser Current authenticated user (from JWT)
	 * @return Success message
	 */
	@PostMapping("/logout-all")
	public ResponseEntity<ResponseModel<String>> logoutAllDevices(
			@AuthenticationPrincipal UserPrincipal currentUser) {
		
		log.info("Logout all devices request for user: {}", currentUser.getEmail());
		
		ResponseModel<String> response = authService.logoutAllDevices(currentUser.getId());
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	// ═══════════════════════════════════════════════════════════════
	//                 OLD REFRESH ENDPOINT (DEPRECATED)
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Old refresh endpoint (DEPRECATED)
	 * 
	 * ⚠️ DEPRECATED: Use /refresh-token endpoint instead
	 * 
	 * WHY DEPRECATED:
	 * - Doesn't use refresh tokens (less secure)
	 * - Requires access token (defeats the purpose)
	 * - No token rotation
	 * - No session management
	 * 
	 * MIGRATION:
	 * OLD: POST /api/auth/refresh (with access token)
	 * NEW: POST /api/auth/refresh-token (with refresh token)
	 * 
	 * This endpoint is kept for backward compatibility but will be removed in future versions.
	 */
	@Deprecated
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshTokenOld(@AuthenticationPrincipal UserPrincipal currentUser) {
		try {
			log.warn("Deprecated /refresh endpoint called. Please use /refresh-token instead.");
			
			String newToken = authService.generateTokenForUser(currentUser);

			JwtResponse jwtResponse = new JwtResponse(
					newToken, 
					currentUser.getId(), 
					currentUser.getUsername(),
					currentUser.getEmail(), 
					currentUser.getPhoneNumber(), 
					currentUser.getAddress(),
					currentUser.getUserRole()
			);

			return ResponseEntity.ok(jwtResponse);
			
		} catch (Exception e) {
			log.error("Error refreshing token (old endpoint)", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new MessageResponse("Error refreshing token!", false));
		}
	}
}