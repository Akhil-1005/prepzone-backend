package com.prepzone.serviceimpl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.prepzone.authentication.RefreshTokenService;
import com.prepzone.authentication.UserPrincipal;
import com.prepzone.constants.Role;
import com.prepzone.entity.RefreshToken;
import com.prepzone.entity.User;
import com.prepzone.exception.TokenRefreshException;
import com.prepzone.repository.UserRepository;
import com.prepzone.request.LoginRequest;
import com.prepzone.request.SignupRequest;
import com.prepzone.request.TokenRefreshRequest;
import com.prepzone.response.JwtResponse;
import com.prepzone.response.TokenRefreshResponse;
import com.prepzone.response.UserResponse;
import com.prepzone.service.AuthService;
import com.prepzone.util.JwtUtils;
import com.prepzone.util.ResponseModel;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;
    
    // ✅ NEW DEPENDENCY #1: Refresh token service
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    // ✅ NEW DEPENDENCY #2: HTTP request (for IP address and user agent)
    @Autowired
    private HttpServletRequest request;

    // ═══════════════════════════════════════════════════════════════
    //                      LOGIN (UPDATED)
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Authenticate user and generate JWT tokens
     * 
     * ✅ UPDATED: Now creates refresh token in addition to access token
     * 
     * CHANGES:
     * 1. Generate access token (15 minutes) - SAME AS BEFORE
     * 2. Generate refresh token (7 days) - ✅ NEW!
     * 3. Store refresh token in database - ✅ NEW!
     * 4. Track IP address and device - ✅ NEW!
     * 5. Return both tokens to user - ✅ UPDATED!
     */
    @Override
    public ResponseModel<JwtResponse> authenticateUser(LoginRequest loginRequest) {
        ResponseModel<JwtResponse> response = new ResponseModel<>();
        try {
            // ✅ STEP 1: Authenticate user (UNCHANGED)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ✅ STEP 2: Generate ACCESS token (UNCHANGED)
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            // ═══════ ✅ NEW: Generate and store REFRESH token ═══════
            
            // STEP 3: Get client info for tracking
            String ipAddress = getClientIP();
            String userAgent = request.getHeader("User-Agent");
            
            log.info("User login from IP: {}, Device: {}", ipAddress, userAgent);
            
            // STEP 4: Create refresh token
            // WHAT IT DOES:
            // - Checks if user has too many sessions (max 5)
            // - If yes, revokes oldest session
            // - Generates new JWT refresh token
            // - Stores in database with IP/device info
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                userPrincipal.getId(), 
                ipAddress, 
                userAgent
            );

            // ✅ STEP 5: Create response with BOTH tokens (UPDATED)
            JwtResponse jwtResponse = new JwtResponse(
                    jwt,                            // Access token (15 min)
                    userPrincipal.getId(),
                    userPrincipal.getUsername(),
                    userPrincipal.getEmail(),
                    userPrincipal.getPhoneNumber(),
                    userPrincipal.getAddress(),
                    userPrincipal.getUserRole()
                    
            );
            
            // STEP 6: Add refresh token to response
            jwtResponse.setRefreshToken(refreshToken.getToken());  // ✅ NEW!

            response.setData(jwtResponse);
            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("User authenticated successfully");

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", loginRequest.getEmail());
            response.setStatusCode(HttpStatus.UNAUTHORIZED.toString());
            response.setMessage("Invalid username or password");
            
        } catch (DisabledException e) {
            log.error("Disabled account attempted login: {}", loginRequest.getEmail());
            response.setStatusCode(HttpStatus.FORBIDDEN.toString());
            response.setMessage("User account is disabled");
            
        } catch (LockedException e) {
            log.error("Locked account attempted login: {}", loginRequest.getEmail());
            response.setStatusCode(HttpStatus.LOCKED.toString());
            response.setMessage("User account is locked");
            
        } catch (Exception e) {
            log.error("Authentication error for user: {}", loginRequest.getEmail(), e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("Authentication failed. Please try again.");
        }
        return response;
    }

    // ═══════════════════════════════════════════════════════════════
    //                   REFRESH TOKEN (NEW METHOD)
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Refresh access token using refresh token
     * 
     * ✅ NEW METHOD
     * 
     * FLOW:
     * 1. Extract refresh token from request
     * 2. Validate JWT format
     * 3. Check if it's actually a refresh token (not access token)
     * 4. Find token in database
     * 5. Verify not expired and not revoked
     * 6. Generate new access token
     * 7. (OPTIONAL) Generate new refresh token (token rotation)
     * 8. Revoke old refresh token
     * 9. Return new tokens
     * 
     * @param request TokenRefreshRequest containing refresh token
     * @return TokenRefreshResponse with new tokens
     */
    public ResponseModel<TokenRefreshResponse> refreshToken(TokenRefreshRequest tokenRequest) {
        ResponseModel<TokenRefreshResponse> response = new ResponseModel<>();
        String requestRefreshToken = tokenRequest.getRefreshToken();
        
        try {
            // ✅ STEP 1: Validate JWT format
            if (!jwtUtils.validateJwtToken(requestRefreshToken)) {
                throw new TokenRefreshException(requestRefreshToken, "Invalid refresh token format");
            }
            
            // ✅ STEP 2: Check if it's actually a refresh token (not access token)
            if (!jwtUtils.isRefreshToken(requestRefreshToken)) {
                throw new TokenRefreshException(requestRefreshToken, "Token is not a refresh token");
            }
            
            // ✅ STEP 3: Find refresh token in database
            RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)  // Verify not expired/revoked
                    .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, 
                        "Refresh token not found in database"));
            
            // ✅ STEP 4: Get user from token
            User user = refreshToken.getUser();
            
            log.info("Refreshing token for user: {}", user.getEmail());
            
            // ✅ STEP 5: Generate new ACCESS token
            // WHY generateTokenFromEmail: We only have email, not full Authentication object
            String newAccessToken = jwtUtils.generateTokenFromEmail(user.getEmail());
            
            // ✅ STEP 6: Generate new REFRESH token (TOKEN ROTATION - More Secure)
            // WHY: If old token was stolen, it becomes invalid immediately
            String ipAddress = getClientIP();
            String userAgent = request.getHeader("User-Agent");
            
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(
                user.getId(), 
                ipAddress, 
                userAgent
            );
            
            // ✅ STEP 7: Revoke old refresh token
            // IMPORTANT: Old token can't be used anymore
            refreshTokenService.revokeToken(requestRefreshToken);
            
            log.info("Token refreshed successfully for user: {}", user.getEmail());
            
            // ✅ STEP 8: Create response with new tokens
            TokenRefreshResponse tokenResponse = new TokenRefreshResponse(
                newAccessToken,              // New access token (15 min)
                newRefreshToken.getToken()   // New refresh token (7 days)
            );
            
            response.setData(tokenResponse);
            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Token refreshed successfully");
            
        } catch (TokenRefreshException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            response.setStatusCode(HttpStatus.FORBIDDEN.toString());
            response.setMessage(e.getMessage());
            
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("Failed to refresh token. Please login again.");
        }
        
        return response;
    }

    // ═══════════════════════════════════════════════════════════════
    //                      REGISTER (UNCHANGED)
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Register new user
     * 
     * ✅ UNCHANGED - No modifications needed
     */
    @Override
    public ResponseModel<UserResponse> registerUser(SignupRequest signUpRequest) {
        ResponseModel<UserResponse> response = new ResponseModel<>();
        try {


            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                response.setStatusCode(HttpStatus.CONFLICT.toString());
                response.setMessage("Email is already in use!");
                return response;
            }

  

            // Create new user
            User user = new User();
            user.setUserName(signUpRequest.getUserName());
            user.setEmail(signUpRequest.getEmail());
            user.setPhoneNumber(signUpRequest.getPhoneNumber());
            user.setSubjectName(signUpRequest.getSubjectName());
            user.setWorkExperince(signUpRequest.getWorkExperince());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setProfilePic(signUpRequest.getProfilePic());

            // Assign role (default Student if not provided)
            Role role = signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.Student;
            user.setRole(role);

            // Save to DB
            User savedUser = userRepository.save(user);

            UserResponse userResponse = new UserResponse(
                    savedUser.getId(),
                    savedUser.getUserName(),
                    savedUser.getEmail(),
                    savedUser.getPhoneNumber(),
                    savedUser.getAddress(),
                    savedUser.getRole()
            );

            response.setData(userResponse);
            response.setStatusCode(HttpStatus.CREATED.toString());
            response.setMessage("User registered successfully");

        } catch (Exception e) {
            log.error("Error during user registration for: {}", signUpRequest.getEmail(), e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("Failed to register user. Please try again.");
        }
        return response;
    }
    
    // ═══════════════════════════════════════════════════════════════
    //                      OTHER METHODS (UNCHANGED)
    // ═══════════════════════════════════════════════════════════════
    
    @Override
    public ResponseModel<SignupRequest> getProfileDetails(UUID userId) {
        // TODO: Implement this method
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phone) {
        return userRepository.existsByPhoneNumber(phone);
    }

    @Override
    public String generateTokenForUser(UserPrincipal userPrincipal) {
        return jwtUtils.generateTokenFromEmail(userPrincipal.getEmail());
    }
    
    // ═══════════════════════════════════════════════════════════════
    //                      LOGOUT (NEW METHOD)
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Logout user - revoke refresh token
     * 
     * ✅ NEW METHOD
     * 
     * WHAT IT DOES:
     * - Revokes the specific refresh token
     * - User is logged out from THIS device only
     * - Other devices remain logged in
     * 
     * @param refreshToken The refresh token to revoke
     * @return Success message
     */
    public ResponseModel<String> logout(String refreshToken) {
        ResponseModel<String> response = new ResponseModel<>();
        try {
            refreshTokenService.revokeToken(refreshToken);
            
            log.info("User logged out successfully");
            
            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Logged out successfully");
            
        } catch (Exception e) {
            log.error("Error during logout", e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("Logout failed");
        }
        return response;
    }
    
    // ═══════════════════════════════════════════════════════════════
    //                 LOGOUT ALL DEVICES (NEW METHOD)
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Logout from all devices
     * 
     * ✅ NEW METHOD
     * 
     * WHAT IT DOES:
     * - Revokes ALL refresh tokens for this user
     * - User is logged out from ALL devices
     * - Must login again on all devices
     * 
     * USE CASES:
     * - User clicks "Logout from all devices" button
     * - User changes password (security measure)
     * - Suspicious activity detected
     * 
     * @param userId User's UUID
     * @return Success message
     */
    public ResponseModel<String> logoutAllDevices(UUID userId) {
        ResponseModel<String> response = new ResponseModel<>();
        try {
            refreshTokenService.revokeAllUserTokens(userId);
            
            log.info("User logged out from all devices: {}", userId);
            
            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Logged out from all devices successfully");
            
        } catch (Exception e) {
            log.error("Error during logout all devices", e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("Logout failed");
        }
        return response;
    }
    
    // ═══════════════════════════════════════════════════════════════
    //                      HELPER METHODS
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Get client IP address
     * 
     * ✅ NEW HELPER METHOD
     * 
     * WHY: Track where logins happen (security)
     * 
     * HANDLES:
     * - Direct connection: request.getRemoteAddr()
     * - Behind proxy/load balancer: X-Forwarded-For header
     * 
     * RETURNS:
     * - "192.168.1.10" (direct connection)
     * - "203.0.113.45" (behind proxy - gets real client IP)
     */
    private String getClientIP() {
        // Check if behind proxy/load balancer
        String xfHeader = request.getHeader("X-Forwarded-For");
        
        if (xfHeader == null) {
            // Direct connection
            return request.getRemoteAddr();
        }
        
        // Behind proxy - get first IP (real client IP)
        // Format: "client_ip, proxy1_ip, proxy2_ip"
        return xfHeader.split(",")[0];
    }
}