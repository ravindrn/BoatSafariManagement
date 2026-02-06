package com.boatsafari.controller;

import com.boatsafari.dto.BookingRequestDTO;
import com.boatsafari.dto.BookingResponseDTO;
import com.boatsafari.dto.BookingUpdateDTO;
import com.boatsafari.model.Booking;
import com.boatsafari.model.BookingStatus;
import com.boatsafari.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    // Add this method to BookingController
    private BookingResponseDTO convertToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
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


        return dto;
    }

    @Autowired
    private BookingService bookingService;

    // GET all bookings
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // GET booking by ID
    // In BookingController.java - enhance the getBookingById method
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            logger.info("Fetching booking details for ID: {}", id);
            Optional<Booking> booking = bookingService.getBookingById(id);

            if (booking.isEmpty()) {
                logger.warn("Booking not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            logger.info("Successfully found booking: {}", booking.get().getId());
            return ResponseEntity.ok(booking.get());

        } catch (Exception e) {
            logger.error("Error fetching booking with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching booking: " + e.getMessage());
        }
    }



    // GET bookings by email
    @GetMapping("/email/{email}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByEmail(@PathVariable String email) {
        try {
            logger.info("Fetching bookings for email: {}", email);
            List<BookingResponseDTO> bookings = bookingService.getBookingsByEmail(email);
            logger.info("Successfully fetched {} bookings for email: {}", bookings.size(), email);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            logger.error("Error fetching bookings for email {}: {}", email, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET bookings by status
    @GetMapping("/status/{status}")
    public List<Booking> getBookingsByStatus(@PathVariable BookingStatus status) {
        return bookingService.getBookingsByStatus(status);
    }

    // POST - Create new booking
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingRequest) {
        try {
            logger.info("Received booking request: {}", bookingRequest);

            // Validate the request
            if (bookingRequest.getTripId() == null) {
                return ResponseEntity.badRequest().body("Trip ID is required");
            }
            if (bookingRequest.getFirstName() == null || bookingRequest.getFirstName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("First name is required");
            }
            if (bookingRequest.getLastName() == null || bookingRequest.getLastName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Last name is required");
            }
            if (bookingRequest.getEmail() == null || bookingRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            if (bookingRequest.getPhoneNumber() == null || bookingRequest.getPhoneNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Phone number is required");
            }
            if (bookingRequest.getNumberOfPeople() == null || bookingRequest.getNumberOfPeople() <= 0) {
                return ResponseEntity.badRequest().body("Number of people must be greater than 0");
            }

            // Check if trip is available
            LocalDateTime tripDate = bookingRequest.getTripDate() != null ?
                    bookingRequest.getTripDate() : LocalDateTime.now().plusDays(1);

            if (!bookingService.isTripAvailable(bookingRequest.getTripId(), tripDate)) {
                return ResponseEntity.badRequest().body("Trip is fully booked for the selected date");
            }

            // Check if requested number of passengers is available
            int availableSeats = bookingService.getAvailableSeats(bookingRequest.getTripId(), tripDate);
            if (bookingRequest.getNumberOfPeople() > availableSeats) {
                return ResponseEntity.badRequest().body("Only " + availableSeats + " seats available for the selected date");
            }

            // Set the trip date if not provided
            if (bookingRequest.getTripDate() == null) {
                bookingRequest.setTripDate(tripDate);
            }

            Booking booking = bookingService.createBooking(bookingRequest);
            return ResponseEntity.ok(booking);

        } catch (RuntimeException e) {
            logger.error("Error creating booking:", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error creating booking:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // PUT - Update booking
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody BookingUpdateDTO updateData) {
        try {
            logger.info("Updating booking {} with data: {}", id, updateData);

            // Validate update data
            if (updateData.getNumberOfPeople() != null && updateData.getNumberOfPeople() <= 0) {
                return ResponseEntity.badRequest().body("Number of people must be greater than 0");
            }

            Booking updatedBooking = bookingService.updateBooking(id, updateData);
            return ResponseEntity.ok(updatedBooking);

        } catch (RuntimeException e) {
            logger.error("Error updating booking {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating booking {}:", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // PUT - Update booking status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestBody BookingStatus status) {
        try {
            logger.info("Updating booking {} status to: {}", id, status);

            Booking updatedBooking = bookingService.updateBookingStatus(id, status);
            return ResponseEntity.ok(updatedBooking);

        } catch (RuntimeException e) {
            logger.error("Error updating booking status {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating booking status {}:", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }



    // DELETE booking
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            logger.info("Deleting booking: {}", id);

            bookingService.deleteBooking(id);
            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            logger.error("Error deleting booking {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error deleting booking {}:", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // GET available seats for a trip
    @GetMapping("/trip/{tripId}/availability")
    public ResponseEntity<?> getAvailableSeats(@PathVariable Long tripId,
                                               @RequestParam(required = false) String tripDate) {
        try {
            LocalDateTime date;
            if (tripDate != null && !tripDate.isEmpty()) {
                date = LocalDateTime.parse(tripDate);
            } else {
                date = LocalDateTime.now().plusDays(1);
            }

            int availableSeats = bookingService.getAvailableSeats(tripId, date);
            return ResponseEntity.ok(availableSeats);

        } catch (Exception e) {
            logger.error("Error getting available seats for trip {}:", tripId, e);
            return ResponseEntity.badRequest().body("Error checking availability");
        }
    }

    // GET check if user can edit booking
    @GetMapping("/user/{email}/can-edit/{bookingId}")
    public ResponseEntity<?> canUserEditBooking(@PathVariable String email, @PathVariable Long bookingId) {
        try {
            logger.info("Checking if user {} can edit booking {}", email, bookingId);

            boolean canEdit = bookingService.canUserEditBooking(bookingId, email);
            return ResponseEntity.ok(canEdit);

        } catch (RuntimeException e) {
            logger.error("Error checking edit permission for booking {}: {}", bookingId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error checking edit permission for booking {}:", bookingId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Booking API is working! Current time: " + LocalDateTime.now());
    }

    // Test data endpoint
    @GetMapping("/test-data")
    public ResponseEntity<List<Booking>> testData() {
        try {
            List<Booking> allBookings = bookingService.getAllBookings();
            logger.info("Total bookings in database: {}", allBookings.size());
            return ResponseEntity.ok(allBookings);
        } catch (Exception e) {
            logger.error("Error in test data endpoint: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET time remaining for editing
    @GetMapping("/{id}/time-remaining")
    public ResponseEntity<?> getTimeRemainingForEdit(@PathVariable Long id, @RequestParam String email) {
        try {
            logger.info("Getting time remaining for editing booking {} by user {}", id, email);

            Duration timeRemaining = bookingService.getTimeRemainingForEdit(id, email);
            return ResponseEntity.ok(timeRemaining.toString());

        } catch (RuntimeException e) {
            logger.error("Error getting time remaining for booking {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error getting time remaining for booking {}:", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }


}