package com.boatsafari.service;

import com.boatsafari.dto.BoatAssignmentRequestDTO;
import com.boatsafari.dto.BoatAssignmentResponseDTO;
import com.boatsafari.model.Boat;
import com.boatsafari.model.BoatAssignment;
import com.boatsafari.model.Booking;
import com.boatsafari.model.User;
import com.boatsafari.repository.BoatAssignmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoatAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(BoatAssignmentService.class);

    @Autowired
    private BoatAssignmentRepository boatAssignmentRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BoatService boatService;

    @Autowired
    private UserService userService;

    // Get all assignments
    public List<BoatAssignment> getAllAssignments() {
        return boatAssignmentRepository.findAll();
    }

    // Get assignment by ID
    public Optional<BoatAssignment> getAssignmentById(Long id) {
        return boatAssignmentRepository.findById(id);
    }

    // Get assignment by booking ID
    public Optional<BoatAssignment> getAssignmentByBookingId(Long bookingId) {
        return boatAssignmentRepository.findByBookingId(bookingId);
    }

    public BoatAssignment createAssignment(BoatAssignmentRequestDTO assignmentRequest, Long staffId) {
        logger.info("Creating boat assignment for booking: {}", assignmentRequest.getBookingId());

        // Get booking
        Booking booking = bookingService.getBookingById(assignmentRequest.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + assignmentRequest.getBookingId()));

        // Get boat
        Boat boat = boatService.getBoatById(assignmentRequest.getBoatId())
                .orElseThrow(() -> new RuntimeException("Boat not found with id: " + assignmentRequest.getBoatId()));

        // Check if booking already has an assignment - FIXED: use assignmentRequest instead of request
        if (boatAssignmentRepository.findByBookingId(assignmentRequest.getBookingId()).isPresent()) {
            throw new RuntimeException("Booking already has a boat assigned");
        }

        // Check if boat is available for the booking date
        if (!isBoatAvailableForDate(boat.getId(), booking.getTripDate())) {
            throw new RuntimeException("Boat is not available for the selected date");
        }

        // Check if boat capacity is sufficient
        if (boat.getCapacity() < booking.getNumberOfPeople()) {
            throw new RuntimeException("Boat capacity (" + boat.getCapacity() + ") is less than number of passengers (" + booking.getNumberOfPeople() + ")");
        }

        BoatAssignment assignment = new BoatAssignment();
        assignment.setBooking(booking);
        assignment.setBoat(boat);

        // Staff ID is optional - can be null
        // Only set assignedBy if staffId is provided
        if (staffId != null) {
            try {
                User staff = userService.getUserById(staffId)
                        .orElseThrow(() -> new RuntimeException("Staff user not found with id: " + staffId));
                assignment.setAssignedBy(staff);
            } catch (Exception e) {
                logger.warn("Could not set assignedBy for staff ID {}: {}", staffId, e.getMessage());
                // Continue without assignedBy - it's optional
            }
        }

        assignment.setNotes(assignmentRequest.getNotes());

        BoatAssignment savedAssignment = boatAssignmentRepository.save(assignment);
        logger.info("Boat assignment created successfully with ID: {}", savedAssignment.getId());
        return savedAssignment;
    }

    // Update assignment
    public BoatAssignment updateAssignment(Long id, BoatAssignmentRequestDTO assignmentRequest, Long staffId) {
        logger.info("Updating boat assignment: {}", id);

        BoatAssignment assignment = boatAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boat assignment not found with id: " + id));

        // Get boat if changed
        if (!assignment.getBoat().getId().equals(assignmentRequest.getBoatId())) {
            Boat boat = boatService.getBoatById(assignmentRequest.getBoatId())
                    .orElseThrow(() -> new RuntimeException("Boat not found with id: " + assignmentRequest.getBoatId()));

            // Check if boat is available for the booking date
            if (!isBoatAvailableForDate(boat.getId(), assignment.getBooking().getTripDate())) {
                throw new RuntimeException("Boat is not available for the selected date");
            }

            // Check if boat capacity is sufficient
            if (boat.getCapacity() < assignment.getBooking().getNumberOfPeople()) {
                throw new RuntimeException("Boat capacity (" + boat.getCapacity() + ") is less than number of passengers (" + assignment.getBooking().getNumberOfPeople() + ")");
            }

            assignment.setBoat(boat);
        }

        // Get staff user if staffId is provided
        if (staffId != null) {
            User staff = userService.getUserById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff user not found with id: " + staffId));
            assignment.setAssignedBy(staff);
        }

        assignment.setNotes(assignmentRequest.getNotes());

        BoatAssignment updatedAssignment = boatAssignmentRepository.save(assignment);
        logger.info("Boat assignment updated successfully");
        return updatedAssignment;
    }

    // Delete assignment
    public void deleteAssignment(Long id) {
        logger.info("Deleting boat assignment: {}", id);

        if (!boatAssignmentRepository.existsById(id)) {
            throw new RuntimeException("Boat assignment not found with id: " + id);
        }

        boatAssignmentRepository.deleteById(id);
        logger.info("Boat assignment deleted successfully");
    }

    // Check if boat is available for a specific date
    public boolean isBoatAvailableForDate(Long boatId, LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

        List<BoatAssignment> assignments = boatAssignmentRepository.findByBoatIdAndTripDateBetween(boatId, startOfDay, endOfDay);
        return assignments.isEmpty();
    }

    // Get assignments by boat ID
    public List<BoatAssignment> getAssignmentsByBoatId(Long boatId) {
        return boatAssignmentRepository.findByBoatId(boatId);
    }

    // Get assignments by boat owner ID
    public List<BoatAssignment> getAssignmentsByBoatOwnerId(Long ownerId) {
        return boatAssignmentRepository.findByBoatOwnerId(ownerId);
    }

    // Convert BoatAssignment to BoatAssignmentResponseDTO
    public BoatAssignmentResponseDTO convertToDTO(BoatAssignment assignment) {
        BoatAssignmentResponseDTO dto = new BoatAssignmentResponseDTO();
        dto.setId(assignment.getId());
        dto.setBookingId(assignment.getBooking().getId());
        dto.setBoat(boatService.convertToDTO(assignment.getBoat()));
        dto.setAssignmentDate(assignment.getAssignmentDate());
        dto.setNotes(assignment.getNotes());
        dto.setCreatedAt(assignment.getCreatedAt());

        if (assignment.getAssignedBy() != null) {
            dto.setAssignedById(assignment.getAssignedBy().getId());
            dto.setAssignedByName(assignment.getAssignedBy().getFirstName() + " " + assignment.getAssignedBy().getLastName());
        }

        return dto;
    }

    // Convert list of assignments to DTOs
    public List<BoatAssignmentResponseDTO> convertToDTOList(List<BoatAssignment> assignments) {
        return assignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}