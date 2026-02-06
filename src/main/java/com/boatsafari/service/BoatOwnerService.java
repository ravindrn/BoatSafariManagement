package com.boatsafari.service;

import com.boatsafari.model.Boat;
import com.boatsafari.model.BoatAssignment;
import com.boatsafari.model.Booking;
import com.boatsafari.model.BookingRevenue;
import com.boatsafari.repository.BoatAssignmentRepository;
import com.boatsafari.repository.BoatRepository;
import com.boatsafari.repository.BookingRepository;
import com.boatsafari.repository.BookingRevenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BoatOwnerService {

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private BoatAssignmentRepository boatAssignmentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingRevenueRepository bookingRevenueRepository;

    @Autowired
    private BookingRevenueService bookingRevenueService;



    // Get all boats owned by a specific owner
    public List<Boat> getBoatsByOwner(Long ownerId) {
        return boatRepository.findByOwnerId(ownerId);
    }



    // In BoatOwnerService.java - fix the getOwnerRevenueStats method
    public Map<String, Object> getOwnerRevenueStats(Long ownerId) {
        List<Boat> ownerBoats = boatRepository.findByOwnerId(ownerId); // ADD THIS LINE

        BookingRevenueService.OwnerRevenueStats stats = bookingRevenueService.getOwnerRevenueStats(ownerId);

        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", stats.getTotalRevenue());
        result.put("totalOwnerPayout", stats.getTotalOwnerPayout());
        result.put("totalCommission", stats.getTotalCommission());
        result.put("completedBookings", stats.getCompletedBookings());
        result.put("commissionRate", "20.0%");
        result.put("totalBoats", ownerBoats.size()); // You can add this if needed

        return result;
    }
    // Get boat assignments with booking details for owner's boats
    public List<Map<String, Object>> getBoatAssignmentsByOwner(Long ownerId) {
        List<Boat> ownerBoats = boatRepository.findByOwnerId(ownerId);
        List<Map<String, Object>> assignmentsWithDetails = new ArrayList<>();

        for (Boat boat : ownerBoats) {
            List<BoatAssignment> assignments = boatAssignmentRepository.findByBoatId(boat.getId());

            for (BoatAssignment assignment : assignments) {
                Map<String, Object> assignmentDetail = new HashMap<>();
                assignmentDetail.put("assignment", assignment);
                assignmentDetail.put("boat", boat);

                // Get booking details from the assignment
                Booking booking = assignment.getBooking();
                if (booking != null) {
                    assignmentDetail.put("booking", booking);
                }

                assignmentsWithDetails.add(assignmentDetail);
            }
        }

        return assignmentsWithDetails;
    }

    // Get revenue for owner's boats
    public List<BookingRevenue> getRevenueByOwner(Long ownerId) {
        List<Boat> ownerBoats = boatRepository.findByOwnerId(ownerId);
        List<BookingRevenue> ownerRevenue = new ArrayList<>();

        for (Boat boat : ownerBoats) {
            // Get assignments for this boat
            List<BoatAssignment> assignments = boatAssignmentRepository.findByBoatId(boat.getId());

            for (BoatAssignment assignment : assignments) {
                // Get revenue for each booking
                Booking booking = assignment.getBooking();
                if (booking != null) {
                    Optional<BookingRevenue> revenue = bookingRevenueRepository.findByBookingId(booking.getId());
                    revenue.ifPresent(ownerRevenue::add);
                }
            }
        }

        return ownerRevenue;
    }

    // Add new boat
    public Boat addBoat(Long ownerId, Boat boat) {
        // Check if registration number already exists
        if (boatRepository.findByRegistrationNumber(boat.getRegistrationNumber()).isPresent()) {
            throw new RuntimeException("Registration number already exists");
        }

        boat.setOwnerId(ownerId);
        boat.setApprovalStatus(Boat.ApprovalStatus.PENDING);
        return boatRepository.save(boat);
    }

    // Update boat
    public Boat updateBoat(Long ownerId, Long boatId, Boat boatDetails) {
        Optional<Boat> optionalBoat = boatRepository.findByIdAndOwnerId(boatId, ownerId);

        if (optionalBoat.isPresent()) {
            Boat boat = optionalBoat.get();

            // Check if new registration number conflicts with other boats
            if (boatDetails.getRegistrationNumber() != null &&
                    !boatDetails.getRegistrationNumber().equals(boat.getRegistrationNumber()) &&
                    boatRepository.existsByRegistrationNumberAndIdNot(boatDetails.getRegistrationNumber(), boatId)) {
                throw new RuntimeException("Registration number already exists");
            }

            // Update fields
            if (boatDetails.getBoatName() != null) boat.setBoatName(boatDetails.getBoatName());
            if (boatDetails.getBoatType() != null) boat.setBoatType(boatDetails.getBoatType());
            if (boatDetails.getCapacity() != null) boat.setCapacity(boatDetails.getCapacity());
            if (boatDetails.getRegistrationNumber() != null) boat.setRegistrationNumber(boatDetails.getRegistrationNumber());
            if (boatDetails.getStatus() != null) boat.setStatus(boatDetails.getStatus());
            if (boatDetails.getHourlyRate() != null) boat.setHourlyRate(boatDetails.getHourlyRate());
            if (boatDetails.getDescription() != null) boat.setDescription(boatDetails.getDescription());
            if (boatDetails.getImageUrl() != null) boat.setImageUrl(boatDetails.getImageUrl());
            if (boatDetails.getAmenities() != null) boat.setAmenities(boatDetails.getAmenities());

            // Reset approval status if important details changed
            if (boatDetails.getBoatName() != null || boatDetails.getBoatType() != null ||
                    boatDetails.getCapacity() != null || boatDetails.getRegistrationNumber() != null) {
                boat.setApprovalStatus(Boat.ApprovalStatus.PENDING);
                boat.setApprovalDate(null);
                boat.setApprovalNotes(null);
                boat.setApprovedBy(null);
            }

            return boatRepository.save(boat);
        }

        throw new RuntimeException("Boat not found or you don't have permission to update this boat");
    }

    // Delete boat
    public void deleteBoat(Long ownerId, Long boatId) {
        Optional<Boat> boat = boatRepository.findByIdAndOwnerId(boatId, ownerId);
        if (boat.isPresent()) {
            // Check if boat has any assignments
            List<BoatAssignment> assignments = boatAssignmentRepository.findByBoatId(boatId);
            if (!assignments.isEmpty()) {
                throw new RuntimeException("Cannot delete boat with existing assignments");
            }
            boatRepository.delete(boat.get());
        } else {
            throw new RuntimeException("Boat not found or you don't have permission to delete this boat");
        }
    }

    // Get boat statistics for owner
    public Map<String, Object> getBoatOwnerStats(Long ownerId) {
        List<Boat> boats = boatRepository.findByOwnerId(ownerId);
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalBoats", boats.size());
        stats.put("availableBoats", boats.stream().filter(b -> b.getStatus() == Boat.BoatStatus.AVAILABLE).count());
        stats.put("boatsInUse", boats.stream().filter(b -> b.getStatus() == Boat.BoatStatus.IN_USE).count());
        stats.put("boatsInMaintenance", boats.stream().filter(b -> b.getStatus() == Boat.BoatStatus.MAINTENANCE).count());

        // Calculate total assignments
        long totalAssignments = 0;
        for (Boat boat : boats) {
            List<BoatAssignment> assignments = boatAssignmentRepository.findByBoatId(boat.getId());
            totalAssignments += assignments.size();
        }
        stats.put("totalAssignments", totalAssignments);

        return stats;
    }

    // Get assignments for a specific boat
    public List<BoatAssignment> getAssignmentsByBoat(Long ownerId, Long boatId) {
        // Verify the boat belongs to the owner
        Optional<Boat> boat = boatRepository.findByIdAndOwnerId(boatId, ownerId);
        if (boat.isPresent()) {
            return boatAssignmentRepository.findByBoatId(boatId);
        }
        throw new RuntimeException("Boat not found or you don't have permission to view assignments");
    }
}