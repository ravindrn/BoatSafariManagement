package com.boatsafari.dto;

import com.boatsafari.model.UserRole;

public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private String username;
    private String email;
    private UserRole role;
    private String redirectUrl;
    private String firstName; // ADD THIS FIELD

    // Constructors
    public AuthResponse() {}

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AuthResponse(boolean success, String message, String token, String username, String email, UserRole role, String redirectUrl, String firstName) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.redirectUrl = redirectUrl;
        this.firstName = firstName; // ADD THIS
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
    public String getFirstName() { return firstName; } // ADD GETTER
    public void setFirstName(String firstName) { this.firstName = firstName; } // ADD SETTER
}