package com.prepzone.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prepzone.constants.Role;
import com.prepzone.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
   
	Optional<User> findByUserNameIgnoreCase(String userName);
    

	Optional<User> findByEmailIgnoreCase(String email);

    Boolean existsByUserName(String userName);

    Boolean existsByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
    

    List<User> findByRole(Role role);
 
    List<User> findByRoleOrderByCreatedOnDesc(Role role);
    

    Boolean existsByPhoneNumber(String phoneNumber);
}