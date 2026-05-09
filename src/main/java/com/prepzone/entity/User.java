package com.prepzone.entity;

import com.prepzone.constants.Role;
import com.prepzone.util.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity
{

	@Column(name="user_name")
    private String userName;
	
	@Column(name="password")
    private String password;
    
    @Column(name="email")
    private String email;
    
    @Column(name="subject_name")
    private String subjectName;
    
    @Column(name="work_experience")
    private String workExperince;
    
    
    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(name="phone_number")
    private String phoneNumber;
    
    @Column(name="address")
    private String address;
 
    
    @Column(name = "profile_pic")
    private String profilePic;

}
