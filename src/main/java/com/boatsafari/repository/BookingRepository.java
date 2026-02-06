package com.boatsafari.repository;

import com.boatsafari.model.Booking;
import com.boatsafari.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // EXISTING METHODS - PRESERVED
    @Query("SELECT b FROM Booking b JOIN FETCH b.trip WHERE b.email = :email")
    List<Booking> findByEmail(@Param("email") String email);

    @Query("SELECT b FROM Booking b WHERE b.email = :email")
    List<Booking> findBookingsByEmail(@Param("email") String email);

    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByTripDateBetween(LocalDateTime start, LocalDateTime end);
    List<Booking> findByTripIdAndTripDateBetween(Long tripId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.trip.id = :tripId")
    List<Booking> findByTripId(@Param("tripId") Long tripId);

    List<Booking> findByStatusAndTripDateBetween(BookingStatus status, LocalDateTime start, LocalDateTime end);

    // NEW METHODS FOR OPERATIONS PORTAL:

    // Count bookings by status
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(@Param("status") BookingStatus status);

    // FIXED: Get revenue by boat - changed b.boat.id to b.trip.boat.id
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.trip.boat.id = :boatId AND b.status = com.boatsafari.model.BookingStatus.COMPLETED")
    Double getRevenueByBoat(@Param("boatId") Long boatId);

    // Get booking statistics by month with COALESCE for null safety
    @Query("SELECT YEAR(b.tripDate) as year, MONTH(b.tripDate) as month, COUNT(b) as bookingCount, " +
            "COALESCE(SUM(b.totalPrice), 0) as revenue " +
            "FROM Booking b WHERE b.status = com.boatsafari.model.BookingStatus.COMPLETED AND b.tripDate IS NOT NULL " +
            "GROUP BY YEAR(b.tripDate), MONTH(b.tripDate) " +
            "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyBookingStats();

    // FIXED: Get bookings with boat assignments - removed LEFT JOIN FETCH b.boat since Booking doesn't have boat relationship
    @Query("SELECT b FROM Booking b WHERE b.status IN (com.boatsafari.model.BookingStatus.CONFIRMED, com.boatsafari.model.BookingStatus.IN_PROGRESS, com.boatsafari.model.BookingStatus.COMPLETED)")
    List<Booking> findBookingsWithBoatAssignments();

    // Get customer booking history
    @Query("SELECT b FROM Booking b WHERE b.email = :email ORDER BY b.tripDate DESC")
    List<Booking> findCustomerBookingHistory(@Param("email") String email);

    // Add this to your existing BookingRepository
    @Query("SELECT t.id, t.name, COUNT(b) as bookingCount, COALESCE(SUM(b.totalPrice), 0) as revenue " +
            "FROM Booking b JOIN b.trip t " +
            "WHERE b.status = com.boatsafari.model.BookingStatus.COMPLETED " +
            "GROUP BY t.id, t.name")
    List<Object[]> getTripRevenueStats();
}