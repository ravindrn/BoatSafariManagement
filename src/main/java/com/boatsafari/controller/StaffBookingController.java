package com.boatsafari.controller;

import com.boatsafari.dto.StaffBookingResponseDTO;
import com.boatsafari.model.Booking;
import com.boatsafari.model.BookingStatus;
import com.boatsafari.service.StaffBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff/bookings")
@CrossOrigin(origins = "*")
public class StaffBookingController {

    private static final Logger logger = LoggerFactory.getLogger(StaffBookingController.class);

    @Autowired
    private StaffBookingService staffBookingService;

    // GET all bookings for staff view
    @GetMapping
    public ResponseEntity<List<StaffBookingResponseDTO>> getAllBookingsForStaff() {
        try {
            List<StaffBookingResponseDTO> bookings = staffBookingService.getAllBookingsForStaff();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            logger.error("Error getting all bookings for staff:", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET bookings by status for staff view
    @GetMapping("/status/{status}")
    public ResponseEntity<List<StaffBookingResponseDTO>> getBookingsByStatusForStaff(@PathVariable BookingStatus status) {
        try {
            List<StaffBookingResponseDTO> bookings = staffBookingService.getBookingsByStatusForStaff(status);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            logger.error("Error getting bookings by status {}:", status, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET bookings by date range for staff view
    @GetMapping("/date-range")
    public ResponseEntity<List<StaffBookingResponseDTO>> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<StaffBookingResponseDTO> bookings = staffBookingService.getBookingsByDateRange(start, end);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            logger.error("Error getting bookings by date range:", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // PUT - Confirm booking
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long id) {
        try {
            logger.info("Confirming booking: {}", id);

            Booking booking = staffBookingService.confirmBooking(id);
            StaffBookingResponseDTO bookingDTO = staffBookingService.convertToStaffDTO(booking);
            return ResponseEntity.ok(bookingDTO);

        } catch (RuntimeException e) {
            logger.error("Error confirming booking {}:", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error confirming booking {}:", id, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    // PUT - Mark booking as in progress
    @PutMapping("/{id}/in-progress")
    public ResponseEntity<?> markAsInProgress(@PathVariable Long id) {
        try {
            logger.info("Marking booking {} as IN_PROGRESS", id);

            Booking booking = staffBookingService.markAsInProgress(id);
            StaffBookingResponseDTO bookingDTO = staffBookingService.convertToStaffDTO(booking);
            return ResponseEntity.ok(bookingDTO);

        } catch (RuntimeException e) {
            logger.error("Error marking booking as in progress {}:", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error marking booking as in progress {}:", id, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    // PUT - Complete booking
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable Long id) {
        try {
            logger.info("Completing booking: {}", id);

            Booking booking = staffBookingService.completeBooking(id);
            StaffBookingResponseDTO bookingDTO = staffBookingService.convertToStaffDTO(booking);
            return ResponseEntity.ok(bookingDTO);

        } catch (RuntimeException e) {
            logger.error("Error completing booking {}:", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error completing booking {}:", id, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    @GetMapping("/revenue-statistics")
    public ResponseEntity<?> getRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            Map<String, Object> stats = staffBookingService.getRevenueStatistics(start, end);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error getting revenue statistics:", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET - Calendar events
    @GetMapping("/calendar")
    public ResponseEntity<List<StaffBookingResponseDTO>> getCalendarBookings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<StaffBookingResponseDTO> bookings;
            if (start != null && end != null) {
                bookings = staffBookingService.getBookingsByDateRange(start, end);
            } else {
                // Default to current month if no date range provided
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime monthStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                LocalDateTime monthEnd = now.plusMonths(1).withDayOfMonth(1).minusDays(1).toLocalDate().atTime(23, 59, 59);
                bookings = staffBookingService.getBookingsByDateRange(monthStart, monthEnd);
            }
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            logger.error("Error getting calendar bookings:", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}