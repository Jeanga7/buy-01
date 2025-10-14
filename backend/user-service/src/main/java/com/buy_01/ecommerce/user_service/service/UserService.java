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
import com.buy_01.ecommerce.user_service.dto.UpdateUserResult;
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
        
        // Seul le premier utilisateur peut être ADMIN
        if (isFirstUser) {
            user.setRole(User.UserRole.ADMIN);
        } else {
            // Pour tous les autres utilisateurs, on s'assure qu'ils ne peuvent pas être ADMIN
            if (user.getRole() == User.UserRole.ADMIN) {
                throw new IllegalArgumentException("Seul le premier utilisateur peut être ADMIN");
            }
            // Si aucun rôle n'est spécifié, on met CLIENT par défaut
            if (user.getRole() == null) {
                user.setRole(User.UserRole.CLIENT);
            }
        }

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
    /**
     * Met à jour un utilisateur existant
     * @param id ID de l'utilisateur à mettre à jour
     * @param userDetails Détails de l'utilisateur à mettre à jour
     * @param currentToken Le token JWT actuel (optionnel, utilisé pour l'invalidation)
     * @return Un objet UpdateUserResult contenant l'utilisateur mis à jour et éventuellement un nouveau token
     * @throws UserNotFoundException si l'utilisateur n'existe pas
     * @throws UserAlreadyExistsException si le nouvel email est déjà utilisé
     * @throws SecurityException si l'utilisateur n'a pas les droits nécessaires
     */
    @Transactional
    public UpdateUserResult updateUser(String id, User userDetails, String currentToken) {
        return userRepository.findById(id)
            .map(existingUser -> {
                boolean emailChanged = userDetails.getEmail() != null && 
                                    !existingUser.getEmail().equals(userDetails.getEmail());
                
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
                
                // Gestion des changements de rôle
                if (userDetails.getRole() != null) {
                    // Vérifier si l'utilisateur actuel est un admin
                    boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                        .getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    
                    // Seul un admin peut modifier les rôles
                    if (!isAdmin) {
                        throw new SecurityException("Seuls les administrateurs peuvent modifier les rôles");
                    }
                    
                    // Empêcher de se promouvoir soi-même
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getName().equals(existingUser.getEmail()) && 
                        userDetails.getRole() == User.UserRole.ADMIN) {
                        throw new SecurityException("Vous ne pouvez pas vous promouvoir vous-même administrateur");
                    }
                    
                    // Mettre à jour le rôle
                    existingUser.setRole(userDetails.getRole());
                }
                
                User updatedUser = userRepository.save(existingUser);
                String newToken = null;
                
                // Si l'email a changé, on invalide l'ancien token et on en génère un nouveau
                if (emailChanged && currentToken != null && currentToken.startsWith("Bearer ")) {
                    String token = currentToken.substring(7);
                    tokenBlacklistService.invalidateToken(token);
                    log.info("Token invalidated for user " + oldEmail + " after email change to " + updatedUser.getEmail());
                    
                    // Générer un nouveau token avec le nouvel email
                    newToken = jwtUtil.generateToken(
                        new org.springframework.security.core.userdetails.User(
                            updatedUser.getEmail(),
                            "",
                            List.of(() -> "ROLE_" + updatedUser.getRole().name())
                        )
                    );
                }
                
                return new UpdateUserResult(updatedUser, newToken);
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




