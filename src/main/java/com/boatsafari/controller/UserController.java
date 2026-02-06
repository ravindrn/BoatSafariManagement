package com.boatsafari.controller;

import com.boatsafari.dto.*;
import com.boatsafari.model.User;
import com.boatsafari.model.UserRole;
import com.boatsafari.service.AuthService;
import com.boatsafari.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    // Public registration - only for CUSTOMER role
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    // Public login - for all roles
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/admin/users/user-managers")
    public ResponseEntity<List<UserResponse>> getAllUserManagers() {
        List<User> userManagers = userService.getAllUserManagers();
        return ResponseEntity.ok(userService.convertToResponseList(userManagers));
    }

    // Admin endpoints for user management

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(userService.convertToResponseList(users));
    }

    @GetMapping("/admin/users/customers")
    public ResponseEntity<List<UserResponse>> getAllCustomers() {
        List<User> customers = userService.getAllCustomers();
        return ResponseEntity.ok(userService.convertToResponseList(customers));
    }

    @GetMapping("/admin/users/staff")
    public ResponseEntity<List<UserResponse>> getAllNonCustomers() {
        List<User> nonCustomers = userService.getAllNonCustomers();
        return ResponseEntity.ok(userService.convertToResponseList(nonCustomers));
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(u -> ResponseEntity.ok(userService.convertToResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/users/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isPresent() && user.get().isActive()) {
            return ResponseEntity.ok(userService.convertToResponse(user.get()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/admin/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request,
                                        @RequestHeader("X-Admin-User") String adminUsername) {
        try {
            System.out.println("=== CREATING USER ===");
            System.out.println("Admin: " + adminUsername);
            System.out.println("User data: " + request.getEmail() + ", " + request.getRole());

            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword()); // In production, encode this
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            user.setRole(request.getRole());
            user.setActive(true); // Default to active

            User savedUser = userService.createUserByAdmin(user, adminUsername);
            System.out.println("User created with ID: " + savedUser.getId());

            return ResponseEntity.ok(userService.convertToResponse(savedUser));
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/admin/users/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        try {
            User user = userService.deactivateUser(id);
            return ResponseEntity.ok(userService.convertToResponse(user));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/admin/users/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        try {
            User user = userService.activateUser(id);
            return ResponseEntity.ok(userService.convertToResponse(user));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Validation endpoints
    @GetMapping("/validate-email/{email}")
    public ResponseEntity<Boolean> validateEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.emailExists(email));
    }

    @GetMapping("/validate-username/{username}")
    public ResponseEntity<Boolean> validateUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.usernameExists(username));
    }
}