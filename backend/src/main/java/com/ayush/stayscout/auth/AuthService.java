package com.ayush.stayscout.auth;

import com.ayush.stayscout.auth.dto.AuthResponse;
import com.ayush.stayscout.auth.dto.LoginRequest;
import com.ayush.stayscout.auth.dto.RegisterRequest;
import com.ayush.stayscout.common.BookingConflictException;
import com.ayush.stayscout.common.UnauthorizedException;
import java.util.Locale;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwt;
    private final GoogleTokenVerifier google;

    public AuthService(UserRepository users, BCryptPasswordEncoder encoder, JwtService jwt, GoogleTokenVerifier google) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.google = google;
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

    public AuthResponse googleLogin(String credential) {
        GoogleTokenVerifier.GoogleIdentity identity = google.verify(credential);
        String email = identity.email().trim().toLowerCase(Locale.ROOT);
        User user = users.findByEmail(email).orElseGet(() -> users.save(
                new User(email, encoder.encode(UUID.randomUUID().toString()), UserRole.USER)));
        return new AuthResponse(jwt.token(user));
    }

    public boolean isGoogleSignInEnabled() {
        return google.isEnabled();
    }
}
