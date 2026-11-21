package com.ayush.stayscout.auth;

import com.ayush.stayscout.common.UnauthorizedException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleTokenVerifier {
    private final String clientId;
    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${app.google.client-id:}") String clientId) {
        this.clientId = clientId == null ? "" : clientId.trim();
        this.verifier = this.clientId.isBlank() ? null : new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(this.clientId))
                .build();
    }

    public boolean isEnabled() {
        return verifier != null;
    }

    public GoogleIdentity verify(String credential) {
        if (!isEnabled()) {
            throw new UnauthorizedException("Google sign-in is not configured");
        }
        try {
            GoogleIdToken token = verifier.verify(credential);
            if (token == null || !Boolean.TRUE.equals(token.getPayload().getEmailVerified())) {
                throw new UnauthorizedException("Google sign-in could not be verified");
            }
            String email = token.getPayload().getEmail();
            if (email == null || email.isBlank()) {
                throw new UnauthorizedException("Google did not provide a verified email address");
            }
            return new GoogleIdentity(token.getPayload().getSubject(), email);
        } catch (UnauthorizedException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new UnauthorizedException("Google sign-in could not be verified");
        }
    }

    public record GoogleIdentity(String subject, String email) { }
}
