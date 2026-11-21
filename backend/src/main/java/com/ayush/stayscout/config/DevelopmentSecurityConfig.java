package com.ayush.stayscout.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain; import org.springframework.security.config.http.SessionCreationPolicy; import com.ayush.stayscout.auth.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class DevelopmentSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwt) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(authorize -> authorize.requestMatchers("/auth/**","/hotels/**","/actuator/health").permitAll().requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated()).addFilterBefore(jwt,org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
