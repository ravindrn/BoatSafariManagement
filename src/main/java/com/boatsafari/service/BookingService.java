package com.boatsafari.service;

import com.boatsafari.dto.BookingRequestDTO;
import com.boatsafari.dto.BookingResponseDTO;
import com.boatsafari.dto.BookingUpdateDTO;
import com.boatsafari.dto.TripDTO;
import com.boatsafari.model.Booking;
import com.boatsafari.model.BookingRevenue;
import com.boatsafari.model.BookingStatus;
import com.boatsafari.model.BookingRevenue.RevenueStatus;
import com.boatsafari.model.Trip;
import com.boatsafari.repository.BookingRepository;
import com.boatsafari.repository.BookingRevenueRepository;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TripService tripService;

    @Autowired
    private BookingRevenueRepository bookingRevenueRepository;

    // Get all bookings
    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        logger.info("Found {} total bookings in database", bookings.size());
        return bookings;
    }

    // Get booking by ID
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    // Get bookings by email - FIXED VERSION
    public List<BookingResponseDTO> getBookingsByEmail(String email) {
        logger.info("Fetching bookings for email: {}", email);

        // Use the method with JOIN FETCH to avoid lazy loading issues
        List<Booking> bookings = bookingRepository.findByEmail(email);
        logger.info("Found {} bookings for email: {}", bookings.size(), email);

        // Convert to DTOs to avoid lazy loading issues
        List<BookingResponseDTO> bookingDTOs = bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return bookingDTOs;
    }

    // Get bookings by status
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    // Create new booking - WORKING VERSION
    public Booking createBooking(BookingRequestDTO bookingRequest) {
        logger.info("Creating booking with request: {}", bookingRequest);

        try {
            // Validate the request
            if (bookingRequest.getTripId() == null) {
                throw new RuntimeException("Trip ID is required");
            }
            if (bookingRequest.getFirstName() == null || bookingRequest.getFirstName().trim().isEmpty()) {
                throw new RuntimeException("First name is required");
            }
            if (bookingRequest.getLastName() == null || bookingRequest.getLastName().trim().isEmpty()) {
                throw new RuntimeException("Last name is required");
            }
            if (bookingRequest.getEmail() == null || bookingRequest.getEmail().trim().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (bookingRequest.getPhoneNumber() == null || bookingRequest.getPhoneNumber().trim().isEmpty()) {
                throw new RuntimeException("Phone number is required");
            }
            if (bookingRequest.getNumberOfPeople() == null || bookingRequest.getNumberOfPeople() <= 0) {
                throw new RuntimeException("Number of people must be greater than 0");
            }

            // Get the trip
            Trip trip = tripService.getTripById(bookingRequest.getTripId())
                    .orElseThrow(() -> new RuntimeException("Trip not found with id: " + bookingRequest.getTripId()));

            logger.info("Found trip: {}", trip.getName());

            // Check if trip is available
            LocalDateTime tripDate = bookingRequest.getTripDate() != null ?
                    bookingRequest.getTripDate() : LocalDateTime.now().plusDays(1);

            if (!isTripAvailable(bookingRequest.getTripId(), tripDate)) {
                throw new RuntimeException("Trip is fully booked for the selected date");
            }

            // Check if requested number of passengers is available
            int availableSeats = getAvailableSeats(bookingRequest.getTripId(), tripDate);
            if (bookingRequest.getNumberOfPeople() > availableSeats) {
                throw new RuntimeException("Only " + availableSeats + " seats available for the selected date");
            }

            // Create booking entity
            Booking booking = new Booking();
            booking.setFirstName(bookingRequest.getFirstName());
            booking.setLastName(bookingRequest.getLastName());
            booking.setEmail(bookingRequest.getEmail());
            booking.setPhoneNumber(bookingRequest.getPhoneNumber());
            booking.setAddress(bookingRequest.getAddress());
            booking.setTrip(trip);
            booking.setNumberOfPeople(bookingRequest.getNumberOfPeople());
            booking.setSpecialRequests(bookingRequest.getSpecialRequests());
            booking.setTripDate(tripDate);
            booking.setStatus(BookingStatus.PENDING);

            // Calculate total price
            booking.calculateTotalPrice();

            logger.info("Saving booking: {}", booking);
            Booking savedBooking = bookingRepository.save(booking);
            logger.info("Booking created successfully with ID: {}", savedBooking.getId());
            return savedBooking;

        } catch (RuntimeException e) {
            logger.error("Error creating booking: {}", e.getMessage());
            throw e; // Re-throw the exception
        } catch (Exception e) {
            logger.error("Unexpected error creating booking:", e);
            throw new RuntimeException("An unexpected error occurred while creating booking");
        }
    }

    // Update booking
    public Booking updateBooking(Long id, BookingUpdateDTO updateData) {
        logger.info("Updating booking {} with data: {}", id, updateData);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        // Check if within 24-hour edit window
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(booking.getCreatedAt(), now);
        if (duration.toHours() >= 24) {
            throw new RuntimeException("Booking can only be modified within 24 hours of creation");
        }

        // Check if booking is in a modifiable status
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking cannot be modified in its current status: " + booking.getStatus());
        }

        // Update fields if provided
        if (updateData.getNumberOfPeople() != null) {
            // Check availability for new passenger count
            int availableSeats = getAvailableSeats(booking.getTrip().getId(), booking.getTripDate());
            int currentBookingSeats = booking.getNumberOfPeople();

            // Calculate net change in seats
            int seatChange = updateData.getNumberOfPeople() - currentBookingSeats;

            if (seatChange > 0 && seatChange > availableSeats) {
                throw new RuntimeException("Not enough seats available for the requested change. Only " + availableSeats + " seats available.");
            }

            booking.setNumberOfPeople(updateData.getNumberOfPeople());
            logger.info("Updated number of people from {} to {}", currentBookingSeats, updateData.getNumberOfPeople());
        }

        if (updateData.getTripDate() != null) {
            // Check if date is changing
            if (!updateData.getTripDate().toLocalDate().equals(booking.getTripDate().toLocalDate())) {
                // Check availability for new date
                if (!isTripAvailable(booking.getTrip().getId(), updateData.getTripDate())) {
                    throw new RuntimeException("Trip is not available on the selected date");
                }
                booking.setTripDate(updateData.getTripDate());
                logger.info("Updated trip date to: {}", updateData.getTripDate());
            }
        }

        if (updateData.getSpecialRequests() != null) {
            booking.setSpecialRequests(updateData.getSpecialRequests());
            logger.info("Updated special requests");
        }

        // Recalculate total price
        booking.calculateTotalPrice();

        logger.info("Updated booking: {}", booking);
        Booking updatedBooking = bookingRepository.save(booking);
        logger.info("Booking updated successfully");
        return updatedBooking;
    }

    // Enhanced edit booking with more validation
    public Booking editBooking(Long bookingId, String userEmail, BookingUpdateDTO updateData) {
        logger.info("Editing booking {} by user {}", bookingId, userEmail);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        // Verify ownership
        if (!booking.getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only edit your own bookings");
        }

        // Check if booking can be edited
        if (!canUserEditBooking(bookingId, userEmail)) {
            throw new RuntimeException("This booking can no longer be edited. Edit window has expired or booking status doesn't allow edits.");
        }

        return updateBooking(bookingId, updateData);
    }

    // Update booking status
    public Booking updateBookingStatus(Long id, BookingStatus status) {
        logger.info("Updating booking {} status to: {}", id, status);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        booking.setStatus(status);

        Booking updatedBooking = bookingRepository.save(booking);
        logger.info("Booking status updated successfully to: {}", status);
        return updatedBooking;
    }

    // Mark booking as in progress
    public Booking markAsInProgress(Long bookingId) {
        logger.info("Marking booking {} as IN_PROGRESS", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Only CONFIRMED bookings can be marked as IN_PROGRESS");
        }

        booking.setStatus(BookingStatus.IN_PROGRESS);
        return bookingRepository.save(booking);
    }

    public Booking completeBooking(Long bookingId) {
        logger.info("Completing booking: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        // Check if booking can be completed
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new RuntimeException("Only CONFIRMED or IN_PROGRESS bookings can be completed");
        }

        // Update booking status
        booking.setStatus(BookingStatus.COMPLETED);
        Booking completedBooking = bookingRepository.save(booking);

        // Create revenue record
        createRevenueRecord(completedBooking);

        logger.info("Booking completed successfully and revenue record created");
        return completedBooking;
    }

    private void createRevenueRecord(Booking booking) {
        try {
            // Check if revenue record already exists using bookingId
            Optional<BookingRevenue> existingRevenue = bookingRevenueRepository.findByBookingId(booking.getId());
            if (existingRevenue.isPresent()) {
                logger.info("Revenue record already exists for booking: {}", booking.getId());
                return;
            }

            BookingRevenue revenue = new BookingRevenue();
            revenue.setBookingId(booking.getId());
            logger.info("Used setBookingId for booking: {}", booking.getId());

            // Set the trip revenue (total price from booking)
            if (booking.getTotalPrice() != null) {
                revenue.setTripRevenue(booking.getTotalPrice());
            } else {
                revenue.setTripRevenue(BigDecimal.ZERO);
            }

            // Set payment date
            revenue.setPaymentDate(LocalDateTime.now());

            // FIXED: Use setRevenueStatus instead of setStatus
            revenue.setRevenueStatus(BookingRevenue.RevenueStatus.PAID);
            logger.info("Set revenue status to PAID for booking: {}", booking.getId());

            // Calculate commission and other amounts
            BigDecimal totalRevenue = booking.getTotalPrice() != null ?
                    booking.getTotalPrice() : BigDecimal.ZERO;

            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                // Calculate tax (10%)
                BigDecimal taxRate = new BigDecimal("0.10");
                BigDecimal taxAmount = totalRevenue.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);

                // Calculate net revenue (after tax)
                BigDecimal netRevenue = totalRevenue.subtract(taxAmount).setScale(2, RoundingMode.HALF_UP);

                // Calculate commission (20% of net revenue)
                BigDecimal commissionRate = new BigDecimal("0.20");
                BigDecimal commissionAmount = netRevenue.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);

                // Calculate owner payout
                BigDecimal ownerPayout = netRevenue.subtract(commissionAmount).setScale(2, RoundingMode.HALF_UP);

                // Set calculated amounts
                revenue.setTaxAmount(taxAmount);
                revenue.setNetRevenue(netRevenue);
                revenue.setCommissionRate(commissionRate);
                revenue.setCommissionAmount(commissionAmount);
                revenue.setOwnerPayout(ownerPayout);
                revenue.setCompanyRevenue(commissionAmount);

                logger.info("Revenue calculated - Total: ${}, Tax: ${}, Net: ${}, Commission: ${}, Owner: ${}",
                        totalRevenue, taxAmount, netRevenue, commissionAmount, ownerPayout);
            }

            // Save to repository
            BookingRevenue savedRevenue = bookingRevenueRepository.save(revenue);
            logger.info("Revenue record created successfully for booking: {}", booking.getId());

        } catch (Exception e) {
            logger.error("Error creating revenue record for booking {}: {}", booking.getId(), e.getMessage());
            // Don't throw exception to avoid rolling back the booking completion
        }
    }

    // Delete booking
    public void deleteBooking(Long id) {
        logger.info("Deleting booking: {}", id);

        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found with id: " + id);
        }

        bookingRepository.deleteById(id);
        logger.info("Booking deleted successfully");
    }

    // Check if trip is available
    public boolean isTripAvailable(Long tripId, LocalDateTime tripDate) {
        logger.debug("Checking availability for trip {} on date {}", tripId, tripDate);

        // Check if there are any bookings for this trip on the same date
        LocalDateTime startOfDay = tripDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = tripDate.toLocalDate().atTime(23, 59, 59);

        List<Booking> existingBookings = bookingRepository.findByTripIdAndTripDateBetween(
                tripId, startOfDay, endOfDay);

        // Get trip capacity
        Trip trip = tripService.getTripById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));

        // Calculate total booked seats for this date (only count confirmed bookings)
        int totalBooked = existingBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.PENDING)
                .mapToInt(Booking::getNumberOfPeople)
                .sum();

        boolean available = totalBooked < trip.getCapacity();
        logger.debug("Trip {} availability on {}: {} (booked: {}/{})",
                tripId, tripDate.toLocalDate(), available, totalBooked, trip.getCapacity());

        return available;
    }

    // Get available seats for a trip
    public int getAvailableSeats(Long tripId, LocalDateTime tripDate) {
        logger.debug("Getting available seats for trip {} on date {}", tripId, tripDate);

        Trip trip = tripService.getTripById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));

        LocalDateTime startOfDay = tripDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = tripDate.toLocalDate().atTime(23, 59, 59);

        List<Booking> existingBookings = bookingRepository.findByTripIdAndTripDateBetween(
                tripId, startOfDay, endOfDay);

        // Only count confirmed and pending bookings
        int totalBooked = existingBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.PENDING)
                .mapToInt(Booking::getNumberOfPeople)
                .sum();

        int availableSeats = trip.getCapacity() - totalBooked;
        logger.debug("Available seats for trip {} on {}: {}", tripId, tripDate.toLocalDate(), availableSeats);

        return availableSeats;
    }

    // Check if user can edit booking
    public boolean canUserEditBooking(Long bookingId, String email) {
        logger.info("Checking if user {} can edit booking {}", email, bookingId);

        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new RuntimeException("Booking not found with id: " + bookingId);
        }

        // Check if booking belongs to the user
        if (!booking.get().getEmail().equals(email)) {
            throw new RuntimeException("Booking does not belong to this user");
        }

        // Check if within 24-hour edit window
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(booking.get().getCreatedAt(), now);
        boolean canEdit = duration.toHours() < 24;

        // Also check if booking is in a modifiable status
        if (canEdit) {
            canEdit = booking.get().getStatus() == BookingStatus.CONFIRMED ||
                    booking.get().getStatus() == BookingStatus.PENDING;
        }

        logger.info("User {} can edit booking {}: {}", email, bookingId, canEdit);
        return canEdit;
    }

    // Get time remaining for editing
    public Duration getTimeRemainingForEdit(Long bookingId, String email) {
        logger.debug("Getting time remaining for editing booking {} by user {}", bookingId, email);

        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new RuntimeException("Booking not found with id: " + bookingId);
        }

        // Check if booking belongs to the user
        if (!booking.get().getEmail().equals(email)) {
            throw new RuntimeException("Booking does not belong to this user");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime editDeadline = booking.get().getCreatedAt().plusHours(24);

        if (now.isAfter(editDeadline)) {
            return Duration.ZERO;
        }

        return Duration.between(now, editDeadline);
    }

    // Get bookings by trip ID
    public List<Booking> getBookingsByTripId(Long tripId) {
        return bookingRepository.findByTripId(tripId);
    }

    // Enhanced DTO conversion method
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

        // Safely convert Trip entity to TripDTO
        if (booking.getTrip() != null) {
            Trip trip = booking.getTrip();
            // Check if it's a proxy and initialize if needed
            if (trip instanceof HibernateProxy) {
                trip = (Trip) ((HibernateProxy) trip).getHibernateLazyInitializer().getImplementation();
            }
            dto.setTrip(convertTripToDTO(trip));
        }

        return dto;
    }

    // Convert Trip entity to TripDTO
    public TripDTO convertTripToDTO(Trip trip) {
        TripDTO tripDTO = new TripDTO();
        tripDTO.setId(trip.getId());
        tripDTO.setName(trip.getName());
        tripDTO.setType(trip.getType());
        tripDTO.setDescription(trip.getDescription());
        tripDTO.setDuration(trip.getDuration());
        tripDTO.setCapacity(trip.getCapacity());
        tripDTO.setPricePerPerson(trip.getPricePerPerson());
        tripDTO.setDestination(trip.getDestination());
        tripDTO.setImageUrl(trip.getImageUrl());
        tripDTO.setFeatured(trip.isFeatured());
        tripDTO.setActive(trip.isActive());
        tripDTO.setCreatedAt(trip.getCreatedAt());
        tripDTO.setUpdatedAt(trip.getUpdatedAt());
        return tripDTO;
    }
}