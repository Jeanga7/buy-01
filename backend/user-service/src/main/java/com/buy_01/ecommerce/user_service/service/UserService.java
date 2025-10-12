package com.buy_01.ecommerce.user_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buy_01.ecommerce.user_service.exception.UserAlreadyExistsException;
import com.buy_01.ecommerce.user_service.exception.UserNotFoundException;
import com.buy_01.ecommerce.user_service.model.User;
import com.buy_01.ecommerce.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service de gestion des utilisateurs
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
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
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
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
    public User updateUser(String id, User userDetails) {
        return userRepository.findById(id)
            .map(existingUser -> {
                // Vérifier si l'email est modifié et s'il est déjà utilisé
                if (!existingUser.getEmail().equals(userDetails.getEmail()) && 
                    userRepository.existsByEmail(userDetails.getEmail())) {
                    throw new UserAlreadyExistsException("Email already in use: " + userDetails.getEmail());
                }
                
                existingUser.setName(userDetails.getName());
                existingUser.setEmail(userDetails.getEmail());
                
                if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                }
                
                if (userDetails.getAvatar() != null) {
                    existingUser.setAvatar(userDetails.getAvatar());
                }
                
                if (userDetails.getRole() != null) {
                    existingUser.setRole(userDetails.getRole());
                }
                
                return userRepository.save(existingUser);
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

