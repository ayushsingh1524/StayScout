package com.ayush.stayscout.auth;
import com.ayush.stayscout.auth.dto.*; import com.ayush.stayscout.common.*; import org.springframework.security.crypto.bcrypt.*; import org.springframework.stereotype.*;
@Service public class AuthService { final UserRepository users; final BCryptPasswordEncoder encoder; final JwtService jwt; public AuthService(UserRepository u,BCryptPasswordEncoder e,JwtService j){users=u;encoder=e;jwt=j;}
 public void register(RegisterRequest r){ if(users.existsByEmail(r.email()))throw new BookingConflictException("Email already registered"); users.save(new User(r.email().toLowerCase(),encoder.encode(r.password()),UserRole.USER)); }
 public AuthResponse login(LoginRequest r){ User u=users.findByEmail(r.email().toLowerCase()).orElseThrow(()->new InvalidRequestException("Invalid email or password")); if(!encoder.matches(r.password(),u.getPasswordHash()))throw new InvalidRequestException("Invalid email or password"); return new AuthResponse(jwt.token(u)); }
}
