package com.boatsafari.repository;

import com.boatsafari.model.Boat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoatRepository extends JpaRepository<Boat, Long> {

    // EXISTING METHODS - PRESERVED
    List<Boat> findByStatus(Boat.BoatStatus status);
    Optional<Boat> findByRegistrationNumber(String registrationNumber);
    List<Boat> findByBoatType(String boatType);
    List<Boat> findByCapacityGreaterThanEqual(Integer minCapacity);

    @Query("SELECT b FROM Boat b WHERE b.status = 'AVAILABLE' AND b.id NOT IN " +
            "(SELECT ba.boat.id FROM BoatAssignment ba WHERE ba.booking.tripDate BETWEEN :startDate AND :endDate)")
    List<Boat> findAvailableBoatsForDate(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Boat b WHERE b.ownerId = :ownerId")
    List<Boat> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Boat b WHERE b.id = :boatId AND b.ownerId = :ownerId")
    Optional<Boat> findByIdAndOwnerId(@Param("boatId") Long boatId, @Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(b) > 0 FROM Boat b WHERE b.registrationNumber = :registrationNumber AND b.id != :excludeId")
    boolean existsByRegistrationNumberAndIdNot(@Param("registrationNumber") String registrationNumber,
                                               @Param("excludeId") Long excludeId);

    List<Boat> findByApprovalStatus(Boat.ApprovalStatus approvalStatus);

    @Query("SELECT b FROM Boat b WHERE LOWER(b.boatName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(b.registrationNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Boat> findByBoatNameContainingIgnoreCaseOrRegistrationNumberContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    // NEW METHODS FOR OPERATIONS PORTAL:

    @Query("SELECT COUNT(b) FROM Boat b WHERE b.approvalStatus = :approvalStatus")
    Long countByApprovalStatus(@Param("approvalStatus") Boat.ApprovalStatus approvalStatus);

    // Count all approved boats
    @Query("SELECT COUNT(b) FROM Boat b WHERE b.approvalStatus = com.boatsafari.model.Boat.ApprovalStatus.APPROVED")
    Long countApprovedBoats();

    // Find boats by owner with approval status
    @Query("SELECT b FROM Boat b WHERE b.ownerId = :ownerId AND b.approvalStatus = :approvalStatus")
    List<Boat> findByOwnerIdAndApprovalStatus(@Param("ownerId") Long ownerId,
                                              @Param("approvalStatus") Boat.ApprovalStatus approvalStatus);

    // Get boat performance statistics
    @Query("SELECT b.boatName, b.boatType, b.capacity, COUNT(ba.id) as assignmentCount " +
            "FROM Boat b LEFT JOIN BoatAssignment ba ON b.id = ba.boat.id " +
            "WHERE b.approvalStatus = com.boatsafari.model.Boat.ApprovalStatus.APPROVED " +
            "GROUP BY b.id, b.boatName, b.boatType, b.capacity " +
            "ORDER BY assignmentCount DESC")
    List<Object[]> getBoatPerformanceStats();
}