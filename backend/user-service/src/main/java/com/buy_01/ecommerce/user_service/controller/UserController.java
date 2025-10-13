package com.buy_01.ecommerce.user_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buy_01.ecommerce.user_service.dto.UserResponse;
import com.buy_01.ecommerce.user_service.exception.UserNotFoundException;
import com.buy_01.ecommerce.user_service.mapper.UserMapper;
import com.buy_01.ecommerce.user_service.model.User;
import com.buy_01.ecommerce.user_service.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Récupère tous les utilisateurs
     * @return Liste de tous les utilisateurs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserResponse> response = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère un utilisateur par son ID
     * @param id ID de l'utilisateur
     * @return Détails de l'utilisateur
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(authentication, #id)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return userService.findById(id)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
    }

    /**
     * Met à jour un utilisateur existant
     * @param id ID de l'utilisateur à mettre à jour
     * @param userDetails Détails de l'utilisateur à mettre à jour
     * @return L'utilisateur mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(authentication, #id)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @RequestBody User userDetails,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User updatedUser = userService.updateUser(id, userDetails, authHeader);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    /**
     * Supprime un utilisateur
     * @param id ID de l'utilisateur à supprimer
     * @return Réponse vide avec statut 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(authentication, #id)")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Promouvoir un utilisateur au rôle d'administrateur
     * @param id ID de l'utilisateur à promouvoir
     * @return L'utilisateur mis à jour avec le rôle ADMIN
     */
    @PostMapping("/{id}/promote-to-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> promoteToAdmin(@PathVariable String id) {
        // Vérifier que l'utilisateur existe
        userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
                
        // Créer un utilisateur avec uniquement le rôle mis à jour
        User updateRequest = new User();
        updateRequest.setRole(User.UserRole.ADMIN);
        User updatedUser = userService.updateUser(id, updateRequest, null);
        
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }
}
