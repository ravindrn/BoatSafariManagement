package com.boatsafari.repository;

import com.boatsafari.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    // EXISTING METHODS - PRESERVED
    List<Trip> findByActiveTrue();
    List<Trip> findByFeaturedTrueAndActiveTrue();

    // NEW METHODS FOR OPERATIONS PORTAL:

    @Query("SELECT t FROM Trip t WHERE t.active = true ORDER BY t.name")
    List<Trip> findAllActiveTrips();

    // Get trip performance statistics with COALESCE for null safety
    @Query("SELECT t.id, t.name, t.type, COUNT(b.id) as bookingCount, " +
            "COALESCE(SUM(b.totalPrice), 0) as totalRevenue, " +
            "COALESCE(AVG(b.numberOfPeople), 0) as avgPassengers " +
            "FROM Trip t LEFT JOIN Booking b ON t.id = b.trip.id AND b.status = com.boatsafari.model.BookingStatus.COMPLETED " +
            "WHERE t.active = true " +
            "GROUP BY t.id, t.name, t.type " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getTripPerformanceStats();

    // Find trips by destination
    @Query("SELECT t FROM Trip t WHERE LOWER(t.destination) LIKE LOWER(CONCAT('%', :destination, '%')) AND t.active = true")
    List<Trip> findByDestinationContainingIgnoreCase(@Param("destination") String destination);

    // Get popular trips (most bookings)
    @Query("SELECT t, COUNT(b.id) as bookingCount " +
            "FROM Trip t LEFT JOIN Booking b ON t.id = b.trip.id " +
            "WHERE t.active = true " +
            "GROUP BY t.id, t.name " +
            "ORDER BY bookingCount DESC")
    List<Object[]> findPopularTrips();

    // Find trips by type
    List<Trip> findByType(String type);

    // Find trips by capacity range
    @Query("SELECT t FROM Trip t WHERE t.capacity >= :minCapacity AND t.active = true")
    List<Trip> findByCapacityGreaterThanEqual(@Param("minCapacity") Integer minCapacity);

    // Find trips by price range
    @Query("SELECT t FROM Trip t WHERE t.pricePerPerson BETWEEN :minPrice AND :maxPrice AND t.active = true")
    List<Trip> findByPricePerPersonBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}