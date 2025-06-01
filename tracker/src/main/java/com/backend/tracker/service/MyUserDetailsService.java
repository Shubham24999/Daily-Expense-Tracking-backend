package com.backend.tracker.service;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.backend.tracker.entity.Users;
import com.backend.tracker.helper.UserPrinciple;
import com.backend.tracker.repository.UsersRepository;


@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(MyUserDetailsService.class.getName());

    @Autowired
    private UsersRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            logger.warning("User not found with username: " + email);
            throw new UsernameNotFoundException("User not found with username: " + email);
        }
        return new UserPrinciple(user.get());   

    }
}
