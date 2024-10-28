package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.dtos.request.LoginRequest;
import es.fjrj3d.seat_booker_api.dtos.request.RegisterRequest;
import es.fjrj3d.seat_booker_api.dtos.response.TokenResponse;
import es.fjrj3d.seat_booker_api.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private final AuthService authService;

    @PostMapping(path = "/register")
    public ResponseEntity<TokenResponse> register (@RequestBody final RegisterRequest request) {
        final TokenResponse token = authService.register(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<TokenResponse> authenticate (@RequestBody final LoginRequest request) {
        final TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping(path = "/refresh")
    public TokenResponse refreshToken (@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        return authService.refreshToken(authHeader);
    }
}
