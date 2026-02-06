package com.boatsafari.repository;

import com.boatsafari.model.User;
import com.boatsafari.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // EXISTING METHODS - PRESERVED
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findByRole(UserRole role); // This already exists - Spring Data JPA derived query
    List<User> findByRoleNot(UserRole role);
    List<User> findByIsActiveTrue();

    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER'")
    List<User> findAllCustomers();

    @Query("SELECT u FROM User u WHERE u.role != 'CUSTOMER'")
    List<User> findAllNonCustomers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER'")
    Long countCustomers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role != 'CUSTOMER'")
    Long countNonCustomers();

    // NEW METHODS FOR OPERATIONS PORTAL:

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") UserRole role);

    // FIXED: Use string enum values instead of enum constants
    @Query("SELECT u FROM User u WHERE u.role IN ('CUSTOMER', 'BOAT_OWNER', 'STAFF', 'OPERATIONS_MANAGER', 'USER_MANAGER')")
    List<User> findAllNonAdminUsers();

    // Search users by name or email
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);

    // Get user statistics by role
    @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.isActive = true GROUP BY u.role")
    List<Object[]> getUserCountByRole();

    // Find users by multiple roles
    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findByRoleIn(@Param("roles") List<UserRole> roles);

    // Find active users by role
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findByRoleAndIsActiveTrue(@Param("role") UserRole role);

    // Count active users by role
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    Long countByRoleAndIsActiveTrue(@Param("role") UserRole role);

    // Find users with last login in date range
    @Query("SELECT u FROM User u WHERE u.lastLogin BETWEEN :startDate AND :endDate")
    List<User> findByLastLoginBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    // Find inactive users (not logged in for a period)
    @Query("SELECT u FROM User u WHERE u.lastLogin < :cutoffDate OR u.lastLogin IS NULL")
    List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Optional: Specific methods for USER_MANAGER - REMOVED DUPLICATE findByRole
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'USER_MANAGER'")
    Long countUserManagers();

    @Query("SELECT u FROM User u WHERE u.role = 'USER_MANAGER' AND u.isActive = true")
    List<User> findActiveUserManagers();
}