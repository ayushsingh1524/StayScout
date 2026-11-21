package com.ayush.stayscout.auth;
import com.ayush.stayscout.auth.dto.*; import com.ayush.stayscout.common.*; import org.springframework.security.crypto.bcrypt.*; import org.springframework.stereotype.*;
@Service public class AuthService { final UserRepository users; final BCryptPasswordEncoder encoder; public AuthService(UserRepository u,BCryptPasswordEncoder e){users=u;encoder=e;}
 public void register(RegisterRequest r){ if(users.existsByEmail(r.email()))throw new BookingConflictException("Email already registered"); users.save(new User(r.email().toLowerCase(),encoder.encode(r.password()),UserRole.USER)); }
 public void login(LoginRequest r){ User u=users.findByEmail(r.email().toLowerCase()).orElseThrow(()->new InvalidRequestException("Invalid email or password")); if(!encoder.matches(r.password(),u.getPasswordHash()))throw new InvalidRequestException("Invalid email or password"); }
}
