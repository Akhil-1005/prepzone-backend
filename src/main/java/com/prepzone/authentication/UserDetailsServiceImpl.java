package com.prepzone.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prepzone.entity.User;
import com.prepzone.repository.UserRepository;



@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Trying to login with: " + username);

        User user = userRepository.findByUserNameIgnoreCase(username).orElse(null);
        if (user == null) {
            user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));
        }

        return UserPrincipal.create(user);
    }

}