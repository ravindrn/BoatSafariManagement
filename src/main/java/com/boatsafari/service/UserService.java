package com.boatsafari.service;

import com.boatsafari.dto.UserResponse;
import com.boatsafari.model.User;
import com.boatsafari.model.UserRole;
import com.boatsafari.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerCustomer(User user) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Force role to CUSTOMER for public registration
        user.setRole(UserRole.CUSTOMER);
        user.setCreatedBy("SYSTEM");

        return userRepository.save(user);
    }

    public User createUserByAdmin(User user, String adminUsername) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Set created by admin
        user.setCreatedBy(adminUsername);

        return userRepository.save(user);
    }

    // Add these methods if needed:
    public List<User> getAllUserManagers() {
        return userRepository.findByRole(UserRole.USER_MANAGER);
    }

    public Long countUserManagers() {
        return userRepository.countByRole(UserRole.USER_MANAGER);
    }

    public List<User> getActiveUserManagers() {
        return userRepository.findByRoleAndIsActiveTrue(UserRole.USER_MANAGER);
    }

    // REMOVE the authenticateUser method completely

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllCustomers() {
        return userRepository.findAllCustomers();
    }

    public List<User> getAllNonCustomers() {
        return userRepository.findAllNonCustomers();
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User deactivateUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(false);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public User activateUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(true);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setLastLogin(user.getLastLogin());
        response.setActive(user.isActive());
        response.setCreatedBy(user.getCreatedBy());
        return response;
    }

    public List<UserResponse> convertToResponseList(List<User> users) {
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
}