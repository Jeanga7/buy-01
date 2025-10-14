package com.buy_01.ecommerce.user_service.controller;

import com.buy_01.ecommerce.user_service.dto.AuthenticationResponse;
import com.buy_01.ecommerce.user_service.dto.LoginRequest;
import com.buy_01.ecommerce.user_service.dto.RegisterRequest;
import com.buy_01.ecommerce.user_service.dto.UserResponse;
import com.buy_01.ecommerce.user_service.exception.UserAlreadyExistsException;
import com.buy_01.ecommerce.user_service.exception.UserAuthenticationException;
import com.buy_01.ecommerce.user_service.exception.UserNotFoundException;
import com.buy_01.ecommerce.user_service.mapper.UserMapper;
import com.buy_01.ecommerce.user_service.model.User;
import com.buy_01.ecommerce.user_service.model.UserSession;
import com.buy_01.ecommerce.user_service.security.JwtUtil;
import com.buy_01.ecommerce.user_service.service.UserService;
import com.buy_01.ecommerce.user_service.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserSessionService userSessionService;

    /**
     * Enregistre un nouvel utilisateur
     * @param registerRequest Les informations d'inscription de l'utilisateur
     * @return L'utilisateur créé avec le statut HTTP 201 (Created)
     */
    /**
     * Enregistre un nouvel utilisateur
     * @param registerRequest Les informations d'inscription de l'utilisateur
     * @return L'utilisateur créé avec le statut HTTP 201 (Created)
     * @throws UserAlreadyExistsException si l'email est déjà utilisé
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Convertir le RegisterRequest en User
        User newUser = userMapper.toUser(registerRequest);
        // Enregistrer l'utilisateur
        User registeredUser = userService.registerUser(newUser);
        // Convertir l'utilisateur enregistré en UserResponse
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(registeredUser));
    }

    /**
     * Authentifie un utilisateur et retourne un token JWT
     * @param loginRequest Les identifiants de connexion
     * @return Un objet contenant le token JWT
     * @throws UserAuthenticationException si l'authentification échoue
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest request) {
            
        // 1. Authentification
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail().toLowerCase().trim(),
                loginRequest.getPassword()
            )
        );

        // 2. Générer un deviceId unique
        String deviceId = userSessionService.generateDeviceId(request, userAgent);
        
        // 3. Invalider toute session existante pour ce device
        userSessionService.invalidateSessionsForDevice(loginRequest.getEmail().toLowerCase().trim(), deviceId);

        // 4. Charger les détails de l'utilisateur
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 5. Générer le token JWT
        String token = jwtUtil.generateToken(userDetails, deviceId);
        
        // 6. Créer une nouvelle session
        userSessionService.createSession(
            userDetails.getUsername(),
            token,
            deviceId,
            request.getRemoteAddr(),
            userAgent,
            jwtUtil.getExpirationInMillis()
        );

        return ResponseEntity.ok(new AuthenticationResponse(
            token, 
            "Connexion réussie",
            deviceId,
            LocalDateTime.now().plusSeconds(jwtUtil.getExpirationInMillis() / 1000)
        ));
    }

    /**
     * Récupère les informations de l'utilisateur actuellement connecté
     * @return Les informations de l'utilisateur
     * @throws UserNotFoundException si l'utilisateur n'est pas trouvé
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email: " + email));
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    /**
     * Déconnecte l'utilisateur en invalidant son token JWT
     * @param authorizationHeader Le header d'autorisation contenant le token JWT
     * @return Un message confirmant la déconnexion
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization") String authorizationHeader) {
            
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            userSessionService.invalidateSession(token);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Déconnexion réussie");
            return ResponseEntity.ok(response);
        }
        throw new BadCredentialsException("Token JWT manquant ou invalide");
    }
    
    @GetMapping("/sessions")
    public ResponseEntity<List<UserSession>> getActiveSessions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        List<UserSession> sessions = userSessionService.getActiveSessions(username);
        return ResponseEntity.ok(sessions);
    }
    
    @PostMapping("/sessions/{sessionId}/revoke")
    public ResponseEntity<Void> revokeSession(@PathVariable String sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        userSessionService.revokeSession(username, sessionId);
        return ResponseEntity.noContent().build();
    }
}
