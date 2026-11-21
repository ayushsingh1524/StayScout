package com.ayush.stayscout.auth;
import com.ayush.stayscout.auth.dto.*; import jakarta.validation.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/auth") public class AuthController { final AuthService service; public AuthController(AuthService s){service=s;} @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public void register(@Valid @RequestBody RegisterRequest r){service.register(r);} @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest r){return service.login(r);} }
