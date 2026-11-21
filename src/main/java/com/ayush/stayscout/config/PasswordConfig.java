package com.ayush.stayscout.config;
import org.springframework.context.annotation.*; import org.springframework.security.crypto.bcrypt.*;
@Configuration public class PasswordConfig { @Bean BCryptPasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); } }
