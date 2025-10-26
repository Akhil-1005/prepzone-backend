package com.prepzone.request;

import com.prepzone.constants.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    private String userName;

    private String email;

    private String password;
    
    private String deviceName;

    private String phoneNumber;
    
    private String address;
    
    private String profilePic;
    
    private Role role = Role.Student; // Default role is USER, can be changed for driver registration
}