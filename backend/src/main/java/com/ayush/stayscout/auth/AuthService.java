package com.ayush.stayscout.auth;

import com.ayush.stayscout.auth.dto.AuthResponse;
import com.ayush.stayscout.auth.dto.LoginRequest;
import com.ayush.stayscout.auth.dto.RegisterRequest;
import com.ayush.stayscout.common.BookingConflictException;
import com.ayush.stayscout.common.UnauthorizedException;
import java.util.Locale;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, BCryptPasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public void register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmail(email)) {
            throw new BookingConflictException("Email already registered");
        }
        users.save(new User(email, encoder.encode(request.password()), UserRole.USER));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        User user = users.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return new AuthResponse(jwt.token(user));
    }
}
