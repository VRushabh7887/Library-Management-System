// filepath: c:\SOFTWARE\PROJECTS\Library-Management-System\src\main\java\com\library\system\service\CustomUserDetailsService.java
package com.library.system.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.library.system.entity.User;
import com.library.system.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // constructor injection

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole()) // Assign roles (e.g., ADMIN, USER)
                .build();
    }
}