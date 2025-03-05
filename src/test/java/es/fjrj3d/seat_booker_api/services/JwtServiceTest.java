package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.models.EUserRole;
import es.fjrj3d.seat_booker_api.models.User;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void set_up() {
        jwtService = new JwtService();
        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String secretKey = Encoders.BASE64.encode(key.getEncoded());

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 7200000L);

        user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(1L);
        Mockito.when(user.getUserName()).thenReturn("testuser");
        Mockito.when(user.getEmail()).thenReturn("test@example.com");
        Mockito.when(user.getRole()).thenReturn(EUserRole.USER);
    }

    @Test
    void generate_token_and_extract_claims() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("test@example.com", extractedUsername);

        String extractedRole = jwtService.extractRole(token);
        assertEquals("USER", extractedRole);

        Date expiration = ReflectionTestUtils.invokeMethod(jwtService, "extractExpiration", token);
        assertNotNull(expiration);
        long diff = expiration.getTime() - System.currentTimeMillis();
        assertTrue(Math.abs(diff - 3600000L) < 5000);
    }

    @Test
    void valid_token_for_correct_user() {
        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user);
        assertTrue(isValid);
    }

    @Test
    void invalid_token_for_incorrect_user() {
        String token = jwtService.generateToken(user);
        User otherUser = Mockito.mock(User.class);
        Mockito.when(otherUser.getId()).thenReturn(2L);
        Mockito.when(otherUser.getUserName()).thenReturn("otherUser");
        Mockito.when(otherUser.getEmail()).thenReturn("other@example.com");
        Mockito.when(otherUser.getRole()).thenReturn(EUserRole.USER);
        boolean isValid = jwtService.isTokenValid(token, otherUser);
        assertFalse(isValid);
    }

    @Test
    void generate_refresh_token() {
        String refreshToken = jwtService.generateRefreshToken(user);
        assertNotNull(refreshToken);
        String extractedUsername = jwtService.extractUsername(refreshToken);
        assertEquals("test@example.com", extractedUsername);
        String extractedRole = jwtService.extractRole(refreshToken);
        assertEquals("USER", extractedRole);
    }

    @Test
    void token_with_custom_expiration() {
        long expirationMillis = 1800000L;
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationMillis);
        String token = jwtService.generateToken(user);
        Date expiration = ReflectionTestUtils.invokeMethod(jwtService, "extractExpiration", token);
        long diff = expiration.getTime() - System.currentTimeMillis();
        assertTrue(Math.abs(diff - expirationMillis) < 5000);
    }

    @Test
    void expired_token_is_invalid() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000L);
        String token = jwtService.generateToken(user);

        Thread.sleep(1500);

        try {
            boolean isValid = jwtService.isTokenValid(token, user);
            assertFalse(isValid);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            assertTrue(true);
        }
    }
}