
package com.prepzone.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

	private String usernameOrEmail;

	private String password;
	
	private String deviceName;
}