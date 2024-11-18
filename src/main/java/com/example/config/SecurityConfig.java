package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.service.CustomDetailsServices;

@Configuration
public class SecurityConfig {

    @Autowired 
    private CustomDetailsServices customDetailsServices;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .requestMatchers("/admin/**").hasAnyRole("ADMIN","MANAGER")  // Admin dashboard restricted to admins
                .requestMatchers("/home", "/profile", "/user/details").authenticated()  // Any authenticated user can access these
                .requestMatchers("/register", "/login",  "/districts").permitAll()  // Publicly accessible pages
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
               .defaultSuccessUrl("/default")  // Redirect after successful login
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")  // Redirect to login after logout
                .invalidateHttpSession(true) // Invalidate the session
                .clearAuthentication(true) // Clear authentication
            .and()
            .csrf().disable();

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customDetailsServices).passwordEncoder(passwordEncoder());
    }
}
