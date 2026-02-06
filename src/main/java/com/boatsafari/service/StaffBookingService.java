package com.boatsafari.service;

import com.boatsafari.dto.StaffBookingResponseDTO;
import com.boatsafari.model.BoatAssignment;
import com.boatsafari.model.Booking;
import com.boatsafari.model.BookingStatus;
import com.boatsafari.repository.BookingRepository;
import com.boatsafari.repository.BookingRevenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StaffBookingService {

    private static final Logger logger = LoggerFactory.getLogger(StaffBookingService.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BoatAssignmentService boatAssignmentService;

    @Autowired
    private BoatService boatService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingRevenueRepository bookingRevenueRepository;

    @Autowired
    private BookingRevenueService bookingRevenueService;

    // Get all bookings for staff view
    public List<StaffBookingResponseDTO> getAllBookingsForStaff() {
        List<Booking> bookings = bookingService.getAllBookings();
        return convertToStaffDTOList(bookings);
    }


    // Get bookings by status for staff view
    public List<StaffBookingResponseDTO> getBookingsByStatusForStaff(BookingStatus status) {
        List<Booking> bookings = bookingService.getBookingsByStatus(status);
        return convertToStaffDTOList(bookings);
    }

    // Get bookings within date range for staff view
    public List<StaffBookingResponseDTO> getBookingsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = bookingService.getAllBookings().stream()
                .filter(booking -> !booking.getTripDate().isBefore(start) && !booking.getTripDate().isAfter(end))
                .collect(Collectors.toList());
        return convertToStaffDTOList(bookings);
    }

    // Confirm booking (change status from PENDING to CONFIRMED)
    public Booking confirmBooking(Long bookingId) {
        logger.info("Confirming booking: {}", bookingId);

        Booking booking = bookingService.getBookingById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only PENDING bookings can be confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingService.updateBookingStatus(bookingId, BookingStatus.CONFIRMED);
        logger.info("Booking confirmed successfully");
        return updatedBooking;
    }

    // Complete booking (staff version)
    public Booking completeBooking(Long bookingId) {
        logger.info("Staff completing booking: {}", bookingId);
        return bookingService.completeBooking(bookingId);
    }

    // Mark booking as in progress
    public Booking markAsInProgress(Long bookingId) {
        logger.info("Staff marking booking {} as IN_PROGRESS", bookingId);
        return bookingService.markAsInProgress(bookingId);
    }

    // Get revenue statistics - FIXED VERSION
    public Map<String, Object> getRevenueStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Get completed bookings in date range
            List<Booking> completedBookings = bookingRepository.findByStatusAndTripDateBetween(
                    BookingStatus.COMPLETED, startDate, endDate);

            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (Booking booking : completedBookings) {
                if (booking.getTotalPrice() != null) {
                    totalRevenue = totalRevenue.add(booking.getTotalPrice());
                }
            }

            // FIXED: Proper commission calculation with tax
            BigDecimal taxRate = new BigDecimal("0.10"); // 10% tax
            BigDecimal commissionRate = new BigDecimal("0.20"); // 20% commission

            // Calculate tax amount (10% of total revenue)
            BigDecimal taxAmount = totalRevenue.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);

            // Calculate net revenue (total revenue - tax)
            BigDecimal netRevenue = totalRevenue.subtract(taxAmount).setScale(2, RoundingMode.HALF_UP);

            // Calculate commission (20% of NET revenue, not total revenue)
            BigDecimal companyRevenue = netRevenue.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);

            // Calculate owner payout (net revenue - commission)
            BigDecimal ownerPayout = netRevenue.subtract(companyRevenue).setScale(2, RoundingMode.HALF_UP);

            BigDecimal averageRevenuePerBooking = completedBookings.isEmpty() ?
                    BigDecimal.ZERO : totalRevenue.divide(
                    BigDecimal.valueOf(completedBookings.size()), 2, RoundingMode.HALF_UP);

            // Convert commission rate to percentage for display
            BigDecimal commissionRatePercent = commissionRate.multiply(new BigDecimal("100"));

            stats.put("totalRevenue", totalRevenue);
            stats.put("companyRevenue", companyRevenue);
            stats.put("ownerPayout", ownerPayout);
            stats.put("completedBookings", completedBookings.size());
            stats.put("averageRevenuePerBooking", averageRevenuePerBooking);
            stats.put("commissionRate", commissionRatePercent); // Now shows as 20.00

        } catch (Exception e) {
            logger.error("Error calculating revenue statistics:", e);
            // Return empty stats
            stats.put("totalRevenue", BigDecimal.ZERO);
            stats.put("companyRevenue", BigDecimal.ZERO);
            stats.put("ownerPayout", BigDecimal.ZERO);
            stats.put("completedBookings", 0);
            stats.put("averageRevenuePerBooking", BigDecimal.ZERO);
            stats.put("commissionRate", new BigDecimal("20.00"));
        }

        return stats;
    }

    // Convert Booking to StaffBookingResponseDTO
    public StaffBookingResponseDTO convertToStaffDTO(Booking booking) {
        StaffBookingResponseDTO dto = new StaffBookingResponseDTO();
        dto.setId(booking.getId());
        dto.setFirstName(booking.getFirstName());
        dto.setLastName(booking.getLastName());
        dto.setEmail(booking.getEmail());
        dto.setPhoneNumber(booking.getPhoneNumber());
        dto.setAddress(booking.getAddress());
        dto.setNumberOfPeople(booking.getNumberOfPeople());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setBookingDate(booking.getBookingDate());
        dto.setTripDate(booking.getTripDate());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());

        // Set trip information
        if (booking.getTrip() != null) {
            dto.setTrip(bookingService.convertTripToDTO(booking.getTrip()));
        }

        // Set boat assignment information if exists
        Optional<BoatAssignment> assignment = boatAssignmentService.getAssignmentByBookingId(booking.getId());
        if (assignment.isPresent()) {
            dto.setBoatName(assignment.get().getBoat().getBoatName());
            dto.setBoatType(assignment.get().getBoat().getBoatType());
            dto.setBoatCapacity(assignment.get().getBoat().getCapacity());
        }

        return dto;
    }

    // Convert list of bookings to staff DTOs
    public List<StaffBookingResponseDTO> convertToStaffDTOList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::convertToStaffDTO)
                .collect(Collectors.toList());
    }
}