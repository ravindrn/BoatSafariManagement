package com.boatsafari.service;

import com.boatsafari.dto.AuthResponse;
import com.boatsafari.dto.LoginRequest;
import com.boatsafari.dto.RegisterRequest;
import com.boatsafari.model.User;
import com.boatsafari.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.boatsafari.repository.UserRepository;

import java.util.UUID;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {
        try {
            System.out.println("=== AUTH SERVICE - REGISTER ===");
            System.out.println("First Name from request: " + request.getFirstName());

            // Create new user as CUSTOMER
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            user.setRole(UserRole.CUSTOMER);
            user.setCreatedBy("SYSTEM");

            System.out.println("User first name before save: " + user.getFirstName());

            User savedUser = userService.registerCustomer(user);

            System.out.println("User first name after save: " + savedUser.getFirstName());

            // Generate simple token
            String token = generateToken(savedUser);

            AuthResponse response = new AuthResponse(
                    true,
                    "Registration successful! You can now login as a customer.",
                    token,
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getRole(),
                    getRedirectUrl(savedUser.getRole()),
                    savedUser.getFirstName() // Make sure this is not null
            );

            System.out.println("AuthResponse first name: " + response.getFirstName());

            return response;

        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            return new AuthResponse(false, e.getMessage());
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            System.out.println("=== LOGIN ATTEMPT ===");
            System.out.println("Email: " + request.getEmail());
            System.out.println("Password: " + request.getPassword());

            // First, check if user exists at all
            Optional<User> userByEmail = userRepository.findByEmail(request.getEmail());
            if (userByEmail.isEmpty()) {
                System.out.println("❌ USER NOT FOUND with email: " + request.getEmail());
                return new AuthResponse(false, "Invalid email or password");
            }

            User user = userByEmail.get();
            System.out.println("✅ USER FOUND: " + user.getEmail());
            System.out.println("📝 User details:");
            System.out.println("   - ID: " + user.getId());
            System.out.println("   - Username: " + user.getUsername());
            System.out.println("   - Password in DB: " + user.getPassword());
            System.out.println("   - Active status: " + user.isActive());
            System.out.println("   - Role: " + user.getRole());

            // Check active status FIRST
            if (!user.isActive()) {
                System.out.println("🚫 ACCOUNT DEACTIVATED - Blocking login");
                return new AuthResponse(false, "Your account is deactivated. Please contact administrator.");
            }

            // Then check password
            System.out.println("🔐 Checking password...");
            System.out.println("   - Input password: " + request.getPassword());
            System.out.println("   - Stored password: " + user.getPassword());
            System.out.println("   - Passwords match: " + request.getPassword().equals(user.getPassword()));

            if (!request.getPassword().equals(user.getPassword())) {
                System.out.println("❌ PASSWORD MISMATCH");
                return new AuthResponse(false, "Invalid email or password");
            }

            // Generate simple token
            String token = generateToken(user);

            System.out.println("🎉 LOGIN SUCCESSFUL for: " + user.getEmail());
            System.out.println("📍 Redirect URL: " + getRedirectUrl(user.getRole()));

            return new AuthResponse(
                    true,
                    "Login successful! Welcome, " + user.getFirstName(),
                    token,
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    getRedirectUrl(user.getRole()),
                    user.getFirstName()
            );

        } catch (Exception e) {
            System.out.println("💥 LOGIN ERROR: " + e.getMessage());
            e.printStackTrace();
            return new AuthResponse(false, "Login failed: " + e.getMessage());
        }
    }

    private String generateToken(User user) {
        // Simple token generation
        return "token-" + user.getId() + "-" + UUID.randomUUID().toString();
    }

    // In AuthService.java - Update getRedirectUrl method
    private String getRedirectUrl(UserRole role) {
        switch (role) {
            case SYSTEM_ADMINISTRATOR:
                return "admin.html";
            case USER_MANAGER:
                return "adminUserManagement.html"; // Direct to User Management
            case STAFF:
                return "schedule.html";
            case BOAT_OWNER:
                return "boats.html";
            case OPERATIONS_MANAGER:
                return "reports.html";
            case CUSTOMER:
            default:
                return "booking.html";
        }
    }
}