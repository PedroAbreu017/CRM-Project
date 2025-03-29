package com.yourcompany.crm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.crm.config.JwtTokenUtil;
import com.yourcompany.crm.dto.ApiResponse;
import com.yourcompany.crm.dto.AuthRequest;
import com.yourcompany.crm.dto.JwtResponse;
import com.yourcompany.crm.dto.RefreshTokenRequest;
import com.yourcompany.crm.dto.SignUpRequest;
import com.yourcompany.crm.exception.TokenRefreshException;
import com.yourcompany.crm.model.RefreshToken;
import com.yourcompany.crm.model.Role;
import com.yourcompany.crm.model.Role.RoleType;
import com.yourcompany.crm.model.User;
import com.yourcompany.crm.repository.RefreshTokenRepository;
import com.yourcompany.crm.repository.RoleRepository;
import com.yourcompany.crm.repository.UserRepository;
import com.yourcompany.crm.service.CustomUserDetailsService;
import com.yourcompany.crm.service.RefreshTokenService;
import com.yourcompany.crm.service.TokenBlacklistService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        log.info("Attempting login for user: {}", request.getUsername());
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);
            
            log.info("Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
            log.error("Login failed for user: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest request) {
        log.debug("Received registration request at /api/auth/register");
        log.info("Starting registration process for user: {}", request.getUsername());

        try {
            // Validação de username
            if (userRepository.existsByUsername(request.getUsername())) {
                log.warn("Username already taken: {}", request.getUsername());
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Username already taken"));
            }

            // Validação de email
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Email already in use: {}", request.getEmail());
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email already in use"));
            }

            // Criação do usuário
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            // Atribuição de role
            Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> {
                    log.error("Default role ROLE_USER not found in database");
                    return new RuntimeException("Error: Role ROLE_USER not found");
                });

            user.getRoles().add(userRole);

            // Salvando o usuário
            userRepository.save(user);
            
            log.info("User registered successfully: {}", request.getUsername());
            return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));

        } catch (Exception e) {
            log.error("Error during registration process for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error during registration: " + e.getMessage()));
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("Attempting to refresh token");
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenRepository.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
                String token = jwtTokenUtil.generateToken(
                    userDetailsService.loadUserByUsername(user.getUsername())
                );
                log.info("Token refreshed successfully for user: {}", user.getUsername());
                return ResponseEntity.ok(new JwtResponse(token, user.getId(), 
                    user.getUsername(), user.getEmail(), requestRefreshToken));
            })
            .orElseThrow(() -> {
                log.error("Refresh token not found: {}", requestRefreshToken);
                return new TokenRefreshException("Refresh token not found");
            });
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        log.info("Processing logout request");
        try {
            String token = bearerToken.substring(7);
            tokenBlacklistService.blacklistToken(token);
            
            String username = jwtTokenUtil.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            refreshTokenService.deleteByUserId(user.getId());
            
            log.info("Logout successful for user: {}", username);
            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            log.error("Error during logout", e);
            return ResponseEntity.badRequest().body("Error during logout");
        }
    }
}

