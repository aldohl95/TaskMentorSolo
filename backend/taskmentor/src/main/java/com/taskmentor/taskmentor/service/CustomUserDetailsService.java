package com.taskmentor.taskmentor.service;

import com.taskmentor.taskmentor.model.User;
import com.taskmentor.taskmentor.repository.UserRepository;
import com.taskmentor.taskmentor.security.AuthUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new AuthUser(user.getUserId(), user.getEmail(), user.getPassword(), user.getAccountType());
    }
}
