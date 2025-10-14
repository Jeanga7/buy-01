package com.buy_01.ecommerce.user_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.buy_01.ecommerce.user_service.dto.PromoteToAdminRequest;
import com.buy_01.ecommerce.user_service.dto.UpdateUserResult;
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
    @PreAuthorize("@userSecurity.canViewUser(authentication, #id)")
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
    @PreAuthorize("@userSecurity.canEditUser(authentication, #id)")
    public ResponseEntity<?> updateUser(
            @PathVariable String id,
            @RequestBody User userDetails,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        UpdateUserResult result = userService.updateUser(id, userDetails, authHeader);
        
        // Si un nouveau token a été généré (changement d'email), on le renvoie dans la réponse
        if (result.getNewToken() != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("user", userMapper.toDto(result.getUser()));
            response.put("token", result.getNewToken());
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.ok(userMapper.toDto(result.getUser()));
    }

    /**
     * Supprime un utilisateur
     * @param id ID de l'utilisateur à supprimer
     * @return Réponse avec un message de confirmation et statut 200 (OK)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@userSecurity.canEditUser(authentication, #id)")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Utilisateur supprimé avec succès");
        return ResponseEntity.ok(response);
    }

        /**
     * Promouvoir un utilisateur au rôle d'administrateur
     * @param id ID de l'utilisateur à promouvoir
     * @return L'utilisateur mis à jour avec le rôle ADMIN
     * @throws UserNotFoundException si l'utilisateur n'est pas trouvé
     * @throws IllegalStateException si l'utilisateur est déjà administrateur
     */
    @PostMapping("/{id}/promote-to-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> promoteToAdmin(
            @PathVariable String id,
            @RequestBody(required = false) PromoteToAdminRequest request) {
        
        // Vérifier que l'utilisateur existe et récupérer ses informations actuelles
        User existingUser = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        
        // Vérifier si l'utilisateur est déjà administrateur
        if (existingUser.getRole() == User.UserRole.ADMIN) {
            throw new IllegalStateException("L'utilisateur est déjà administrateur");
        }
        
        // Mettre à jour uniquement le rôle
        existingUser.setRole(User.UserRole.ADMIN);
        UpdateUserResult result = userService.updateUser(id, existingUser, null);
        
        return ResponseEntity.ok(userMapper.toDto(result.getUser()));
    }
}
