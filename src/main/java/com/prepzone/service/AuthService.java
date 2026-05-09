package com.prepzone.service;

import java.util.UUID;

import com.prepzone.authentication.UserPrincipal;
import com.prepzone.request.LoginRequest;
import com.prepzone.request.SignupRequest;
import com.prepzone.response.JwtResponse;
import com.prepzone.response.UserResponse;
import com.prepzone.util.ResponseModel;

public interface AuthService {

	ResponseModel<JwtResponse> authenticateUser( LoginRequest loginRequest);

	ResponseModel<UserResponse> registerUser( SignupRequest signUpRequest);

	String generateTokenForUser(UserPrincipal currentUser);

	boolean existsByPhoneNumber(String phoneNumber);

	boolean existsByEmail(String email);
	
	ResponseModel<SignupRequest> getProfileDetails(UUID userId);
	
	

}
