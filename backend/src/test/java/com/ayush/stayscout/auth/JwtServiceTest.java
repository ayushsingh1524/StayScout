package com.ayush.stayscout.auth;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.*; import java.lang.reflect.*;
class JwtServiceTest { @Test void createsReadableToken() throws Exception { JwtService jwt=new JwtService("01234567890123456789012345678901"); User u=new User("a@b.com","hash",UserRole.USER); Field id=User.class.getDeclaredField("id"); id.setAccessible(true); id.set(u,1L); String token=jwt.token(u); assertEquals("1",jwt.claims(token).getSubject()); } }
