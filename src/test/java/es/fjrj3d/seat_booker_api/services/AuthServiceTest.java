package es.fjrj3d.seat_booker_api.services;

import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import es.fjrj3d.seat_booker_api.dtos.request.LoginRequest;
import es.fjrj3d.seat_booker_api.dtos.request.RegisterRequest;
import es.fjrj3d.seat_booker_api.dtos.response.TokenResponse;
import es.fjrj3d.seat_booker_api.models.EUserRole;
import es.fjrj3d.seat_booker_api.models.Token;
import es.fjrj3d.seat_booker_api.models.User;
import es.fjrj3d.seat_booker_api.repositories.ITokenRepository;
import es.fjrj3d.seat_booker_api.repositories.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;
    private IUserRepository iUserRepository;
    private ITokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void set_up() {
        iUserRepository = Mockito.mock(IUserRepository.class);
        tokenRepository = Mockito.mock(ITokenRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtService = Mockito.mock(JwtService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);

        authService = new AuthService(iUserRepository, tokenRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    void register_creates_user_and_returns_token_response() {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");
        Mockito.when(iUserRepository.count()).thenReturn(0L);
        Mockito.when(passwordEncoder.encode("password123")).thenReturn("encoded_password123");

        User user = User.builder()
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .password("encoded_password123")
                .role(EUserRole.ADMIN)
                .build();
        Mockito.when(iUserRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(jwtService.generateToken(Mockito.any(User.class))).thenReturn("jwt_token");
        Mockito.when(jwtService.generateRefreshToken(Mockito.any(User.class))).thenReturn("refresh_token");
        Mockito.when(tokenRepository.save(Mockito.any(Token.class))).thenReturn(Token.builder().build());

        try (MockedStatic<Customer> customerMock = Mockito.mockStatic(Customer.class)) {
            Customer mockCustomer = Mockito.mock(Customer.class);
            Mockito.when(mockCustomer.getId()).thenReturn("stripe_customer_123");
            customerMock.when(() -> Customer.create(Mockito.any(CustomerCreateParams.class))).thenReturn(mockCustomer);

            TokenResponse response = authService.register(request);
            assertNotNull(response);
            assertEquals("jwt_token", response.accessToken());
            assertEquals("refresh_token", response.refreshToken());
        }
    }

    @Test
    void login_returns_token_response_for_valid_credentials() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        User user = User.builder()
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .password("encoded_password123")
                .role(EUserRole.USER)
                .build();
        Mockito.when(iUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        Mockito.when(jwtService.generateToken(user)).thenReturn("jwt_token");
        Mockito.when(jwtService.generateRefreshToken(user)).thenReturn("refresh_token");
        Mockito.when(tokenRepository.save(Mockito.any(Token.class))).thenReturn(Token.builder().build());

        TokenResponse response = authService.login(request);
        assertNotNull(response);
        assertEquals("jwt_token", response.accessToken());
        assertEquals("refresh_token", response.refreshToken());
    }

    @Test
    void login_throws_exception_for_invalid_credentials() {
        LoginRequest request = new LoginRequest("test@example.com", "wrongpassword");
        Mockito.doThrow(new RuntimeException("Authentication failed"))
                .when(authenticationManager)
                .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
        Exception ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Authentication failed", ex.getMessage());
    }

    @Test
    void refresh_token_returns_token_response_for_valid_refresh_token() {
        String refreshToken = "refresh_token";
        String authHeader = "Bearer " + refreshToken;
        Mockito.when(jwtService.extractUsername(refreshToken)).thenReturn("test@example.com");
        User user = User.builder()
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .role(EUserRole.USER)
                .build();
        Mockito.when(iUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        Mockito.when(jwtService.isTokenValid(refreshToken, user)).thenReturn(true);
        Mockito.when(jwtService.generateRefreshToken(user)).thenReturn("new_access_token");
        Mockito.when(tokenRepository.save(Mockito.any(Token.class))).thenReturn(Token.builder().build());

        TokenResponse response = authService.refreshToken(authHeader);
        assertNotNull(response);
        assertEquals("new_access_token", response.accessToken());
        assertEquals(refreshToken, response.refreshToken());
    }

    @Test
    void refresh_token_throws_exception_for_invalid_auth_header() {
        String invalidHeader = "InvalidHeader";
        Exception ex = assertThrows(IllegalArgumentException.class, () -> authService.refreshToken(invalidHeader));
        assertEquals("Invalid auth header", ex.getMessage());
    }

    @Test
    void refresh_token_returns_null_if_username_is_null() {
        String refreshToken = "refresh_token";
        String authHeader = "Bearer " + refreshToken;
        Mockito.when(jwtService.extractUsername(refreshToken)).thenReturn(null);
        TokenResponse response = authService.refreshToken(authHeader);
        assertNull(response);
    }

    @Test
    void refresh_token_returns_null_if_token_invalid() {
        String refreshToken = "refresh_token";
        String authHeader = "Bearer " + refreshToken;
        Mockito.when(jwtService.extractUsername(refreshToken)).thenReturn("test@example.com");
        User user = User.builder()
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .role(EUserRole.USER)
                .build();
        Mockito.when(iUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        Mockito.when(jwtService.isTokenValid(refreshToken, user)).thenReturn(false);

        TokenResponse response = authService.refreshToken(authHeader);
        assertNull(response);
    }
}