package com.example.service;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.model.User;
import com.example.repository.UserRepository;

@Service
public class CustomDetailsServices implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting to load user: " + username);
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("Invalid Username or Password");
        }
        
        return new CustomUserDetails(
            user.getUsername(),
            user.getPassword(),
            authorities(user.getRole()),
            user.getFullname(),
            user.getRole()
        );
    }


    public Collection<? extends GrantedAuthority> authorities(String role) {  
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
