package com.prode.infrastructure.adapter.inbound.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prode.application.dto.request.LoginRequest;
import com.prode.application.dto.request.TokenRefreshRequest;
import com.prode.application.dto.request.UserRequest;
import com.prode.application.dto.response.LoginResponse;
import com.prode.application.dto.response.UserResponse;
import com.prode.application.service.UserService;
import com.prode.infrastructure.adapter.outbound.persistence.entity.RefreshTokenEntity;
import com.prode.infrastructure.security.JwtService;
import com.prode.infrastructure.security.RefreshTokenService;
import com.prode.infrastructure.security.TokenBlacklistService;
import com.prode.shared.dto.ApiResult;
import com.prode.shared.exception.BusinessException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Gestion de usuarios y tokens")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    public UserController(UserService userService, AuthenticationManager authenticationManager,
            JwtService jwtService, UserDetailsService userDetailsService,
            RefreshTokenService refreshTokenService, TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario")
    public ResponseEntity<ApiResult<UserResponse>> register(@Valid @RequestBody UserRequest request) {
        UserResponse user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.ok("Usuario registrado exitosamente", user));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion y obtener token JWT")
    public ResponseEntity<ApiResult<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasenia()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(request.getEmail());

        ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", jwtToken)
                .httpOnly(true).secure(false).path("/").maxAge(7 * 24 * 60 * 60).sameSite("Lax").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(ApiResult.ok("Login exitoso", new LoginResponse(jwtToken, refreshToken.getToken())));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Obtener nuevo JWT usando Refresh Token")
    public ResponseEntity<ApiResult<LoginResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUsuario)
                .map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    String token = jwtService.generateToken(userDetails);

                    // CORRECCIÓN: Actualizar la Cookie HttpOnly también en el refresh
                    ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", token)
                            .httpOnly(true).secure(false).path("/").maxAge(7 * 24 * 60 * 60).sameSite("Lax").build();

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                            .body(ApiResult.ok("Token refrescado",
                                    new LoginResponse(token, request.getRefreshToken())));
                })
                .orElseThrow(() -> new BusinessException("Refresh token no valido o expirado"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion (revoca el token actual)")
    public ResponseEntity<ApiResult<Void>> logout(HttpServletRequest request) {
        String jwt = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName()))
                    jwt = cookie.getValue();
            }
        }

        if (jwt != null) {
            tokenBlacklistService.blacklistToken(jwt);
            try {
                String email = jwtService.extractUsername(jwt);
                refreshTokenService.deleteByUserEmail(email);
            } catch (Exception ignored) {
            }
        }

        ResponseCookie cleanCookie = ResponseCookie.from("jwt_token", "")
                .httpOnly(true).secure(false).path("/").maxAge(0).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(ApiResult.ok("Sesion cerrada correctamente", null));
    }
}

