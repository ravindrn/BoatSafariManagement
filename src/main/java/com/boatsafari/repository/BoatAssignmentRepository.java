package com.boatsafari.repository;

import com.boatsafari.model.BoatAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoatAssignmentRepository extends JpaRepository<BoatAssignment, Long> {

    // EXISTING METHODS - PRESERVED
    List<BoatAssignment> findByBoatId(Long boatId);

    @Query("SELECT ba FROM BoatAssignment ba WHERE ba.booking.id = :bookingId")
    Optional<BoatAssignment> findByBookingId(@Param("bookingId") Long bookingId);

    @Query("SELECT COUNT(ba) > 0 FROM BoatAssignment ba WHERE ba.booking.id = :bookingId")
    boolean existsByBookingId(@Param("bookingId") Long bookingId);

    @Query("SELECT ba FROM BoatAssignment ba WHERE ba.boat.ownerId = :ownerId")
    List<BoatAssignment> findByBoatOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT ba FROM BoatAssignment ba WHERE ba.boat.id = :boatId AND ba.booking.tripDate BETWEEN :startDate AND :endDate")
    List<BoatAssignment> findByBoatIdAndTripDateBetween(
            @Param("boatId") Long boatId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // NEW METHODS FOR OPERATIONS PORTAL:

    @Query("SELECT COUNT(ba) FROM BoatAssignment ba WHERE ba.boat.id = :boatId")
    Long countByBoatId(@Param("boatId") Long boatId);

    // Get assignment statistics by boat
    @Query("SELECT ba.boat.id, COUNT(ba) as assignmentCount " +
            "FROM BoatAssignment ba " +
            "GROUP BY ba.boat.id")
    List<Object[]> getAssignmentCountByBoat();

    // Get assignments within date range
    @Query("SELECT ba FROM BoatAssignment ba WHERE ba.assignmentDate BETWEEN :start AND :end")
    List<BoatAssignment> findAssignmentsByDateRange(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    // Get boat utilization statistics
    @Query("SELECT b.id, b.boatName, COUNT(ba.id) as totalAssignments, " +
            "COALESCE(AVG(DATEDIFF(ba.booking.tripDate, ba.assignmentDate)), 0) as avgAssignmentLeadTime " +
            "FROM Boat b LEFT JOIN BoatAssignment ba ON b.id = ba.boat.id " +
            "WHERE b.approvalStatus = com.boatsafari.model.Boat.ApprovalStatus.APPROVED " +
            "GROUP BY b.id, b.boatName")
    List<Object[]> getBoatUtilizationStats();
}