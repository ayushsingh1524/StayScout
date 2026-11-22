package com.ayush.stayscout.auth;

import com.ayush.stayscout.auth.dto.AuthResponse;
import com.ayush.stayscout.auth.dto.GoogleAuthConfigResponse;
import com.ayush.stayscout.auth.dto.GoogleCredentialRequest;
import com.ayush.stayscout.auth.dto.LoginRequest;
import com.ayush.stayscout.auth.dto.RegisterRequest;
import com.ayush.stayscout.common.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;
    private final AuthenticationRateLimiter rateLimiter;
    private final String googleClientId;

    public AuthController(
            AuthService service,
            AuthenticationRateLimiter rateLimiter,
            @Value("${app.google.client-id:}") String googleClientId
    ) {
        this.service = service;
        this.rateLimiter = rateLimiter;
        this.googleClientId = googleClientId == null ? "" : googleClientId.trim();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        ensureAllowed(rateLimiter.registrationAllowed(clientIp(httpRequest)));
        service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        ensureAllowed(rateLimiter.loginAllowed(clientIp(httpRequest)));
        return service.login(request);
    }

    @GetMapping("/google/config")
    public GoogleAuthConfigResponse googleConfig() {
        return new GoogleAuthConfigResponse(service.isGoogleSignInEnabled() ? googleClientId : "", service.isGoogleSignInEnabled());
    }

    @PostMapping("/google")
    public AuthResponse googleLogin(@Valid @RequestBody GoogleCredentialRequest request) {
        return service.googleLogin(request.credential());
    }

    private void ensureAllowed(boolean allowed) {
        if (!allowed) {
            throw new TooManyRequestsException("Too many authentication attempts. Please try again in one minute.");
        }
    }

    private String clientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
