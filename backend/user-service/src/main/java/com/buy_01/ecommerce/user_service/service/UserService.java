package com.buy_01.ecommerce.user_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buy_01.ecommerce.user_service.dto.LoginRequest;
import com.buy_01.ecommerce.user_service.exception.UserAlreadyExistsException;
import com.buy_01.ecommerce.user_service.exception.UserNotFoundException;
import com.buy_01.ecommerce.user_service.model.User;
import com.buy_01.ecommerce.user_service.security.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import com.buy_01.ecommerce.user_service.repository.UserRepository;
import com.buy_01.ecommerce.user_service.security.JwtUtil;

import lombok.RequiredArgsConstructor;

/**
 * Service de gestion des utilisateurs
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Trouve un utilisateur par son ID
     * @param id L'ID de l'utilisateur à rechercher
     * @return Un Optional contenant l'utilisateur s'il existe, vide sinon
     */
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    /**
     * Enregistre un nouvel utilisateur
     * @param user Les détails de l'utilisateur à enregistrer
     * @return L'utilisateur enregistré avec son ID généré
     * @throws UserAlreadyExistsException si l'email est déjà utilisé
     * @throws IllegalArgumentException si les données de l'utilisateur sont invalides
     * 
     * @example
     * User newUser = new User();
     * newUser.setEmail("user@example.com");
     * newUser.setPassword("password123");
     * newUser.setName("John Doe");
     * User savedUser = userService.registerUser(newUser);
     */
    /**
     * Authentifie un utilisateur et génère un token JWT
     * @param loginRequest Les identifiants de connexion
     * @return Le token JWT
     */
    public String authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        return jwtUtil.generateToken(userDetails);
    }

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + user.getEmail());
        }

        // Vérifier si c'est le premier utilisateur
        boolean isFirstUser = userRepository.count() == 0;
        
        // Si c'est le premier utilisateur, on le définit comme administrateur
        if (isFirstUser) {
            user.setRole(User.UserRole.ADMIN);
        } else if (user.getRole() == null) {
            // Si aucun rôle n'est spécifié, on met CLIENT par défaut
            user.setRole(User.UserRole.CLIENT);
        }
        // Sinon, on garde le rôle spécifié (SELLER ou autre)

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    /**
     * Déconnecte un utilisateur en invalidant son token JWT
     * @param token Le token JWT à invalider
     */
    @Transactional
    public void logout(String token) {
        // Ajouter le token à la liste noire
        tokenBlacklistService.invalidateToken(token);
        // Invalider la session en cours
        SecurityContextHolder.clearContext();
    }

    /**
     * Met à jour un utilisateur existant
     * @param id ID de l'utilisateur à mettre à jour
     * @param userDetails Détails de l'utilisateur à mettre à jour
     * @return L'utilisateur mis à jour
     * @throws UserNotFoundException si l'utilisateur n'existe pas
     * @throws UserAlreadyExistsException si le nouvel email est déjà utilisé
     * @throws IllegalArgumentException si l'ID est invalide
     */
    @Transactional
    public User updateUser(String id, User userDetails, String currentToken) {
        return userRepository.findById(id)
            .map(existingUser -> {
                boolean emailChanged = !existingUser.getEmail().equals(userDetails.getEmail());
                
                // Vérifier si l'email est modifié et s'il est déjà utilisé
                if (emailChanged && userRepository.existsByEmail(userDetails.getEmail())) {
                    throw new UserAlreadyExistsException("Email already in use: " + userDetails.getEmail());
                }
                
                // Sauvegarder l'ancien email pour le journal
                String oldEmail = existingUser.getEmail();
                
                // Mettre à jour uniquement les champs fournis
                if (userDetails.getName() != null) {
                    existingUser.setName(userDetails.getName());
                }
                
                if (emailChanged) {
                    existingUser.setEmail(userDetails.getEmail());
                }
                
                if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                }
                
                if (userDetails.getAvatar() != null) {
                    existingUser.setAvatar(userDetails.getAvatar());
                }
                
                // Seul un admin peut changer le rôle et il doit être fourni explicitement
                if (userDetails.getRole() != null && 
                    SecurityContextHolder.getContext().getAuthentication()
                        .getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    existingUser.setRole(userDetails.getRole());
                }
                
                User updatedUser = userRepository.save(existingUser);
                
                // Si l'email a changé, on invalide l'ancien token
                if (emailChanged && currentToken != null && currentToken.startsWith("Bearer ")) {
                    String token = currentToken.substring(7);
                    tokenBlacklistService.invalidateToken(token);
                    log.info("Token invalidated for user " + oldEmail + " after email change to " + updatedUser.getEmail());
                }
                
                return updatedUser;
            })
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    /**
     * Supprime un utilisateur par son ID
     * @param id ID de l'utilisateur à supprimer
     * @throws UserNotFoundException si l'utilisateur n'existe pas
     */
    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
    
    /**
     * Supprime un utilisateur par son email
     * @param email Email de l'utilisateur à supprimer
     * @throws UserNotFoundException si l'utilisateur n'existe pas
     */
    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        userRepository.delete(user);
    }
}




