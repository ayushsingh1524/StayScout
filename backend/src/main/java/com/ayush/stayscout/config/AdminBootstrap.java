package com.ayush.stayscout.config;

import com.ayush.stayscout.auth.User;
import com.ayush.stayscout.auth.UserRepository;
import com.ayush.stayscout.auth.UserRole;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminBootstrap {

    @Bean
    CommandLineRunner bootstrapAdmin(
            UserRepository users,
            BCryptPasswordEncoder encoder,
            @Value("${BOOTSTRAP_ADMIN_EMAIL:}") String email,
            @Value("${BOOTSTRAP_ADMIN_PASSWORD:}") String password
    ) {
        return arguments -> {
            if (email.isBlank() || password.isBlank()) {
                return;
            }
            String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
            if (!users.existsByEmail(normalizedEmail)) {
                users.save(new User(normalizedEmail, encoder.encode(password), UserRole.ADMIN));
            }
        };
    }
}
