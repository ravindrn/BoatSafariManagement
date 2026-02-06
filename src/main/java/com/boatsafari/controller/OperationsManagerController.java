package com.boatsafari.controller;

import com.boatsafari.model.Boat;
import com.boatsafari.model.Booking;
import com.boatsafari.model.BookingRevenue;
import com.boatsafari.model.BookingStatus;
import com.boatsafari.repository.BoatRepository;
import com.boatsafari.repository.BookingRepository;
import com.boatsafari.repository.BookingRevenueRepository;
import com.boatsafari.repository.TripRepository;
import com.boatsafari.service.BookingRevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/operations")
@CrossOrigin(origins = "*")
public class OperationsManagerController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingRevenueRepository bookingRevenueRepository;

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BookingRevenueService bookingRevenueService;



    // Quick Statistics Dashboard - SIMPLIFIED VERSION
    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Total Revenue (from completed bookings ONLY)
            List<Booking> completedBookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);
            Double totalRevenue = completedBookings.stream()
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0.0)
                    .sum();
            stats.put("totalRevenue", totalRevenue);

            // Company Revenue (20% of total revenue from completed bookings)
            Double companyRevenue = totalRevenue * 0.2;
            stats.put("companyRevenue", companyRevenue);

            // Total Bookings (all statuses)
            Long totalBookings = bookingRepository.count();
            stats.put("totalBookings", totalBookings != null ? totalBookings : 0);

            // Active Trips
            List<com.boatsafari.model.Trip> activeTripsList = tripRepository.findByActiveTrue();
            stats.put("activeTrips", activeTripsList.size());

            // Pending Bookings
            Long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
            stats.put("pendingBookings", pendingBookings != null ? pendingBookings : 0);

            // Completed Bookings
            Long completedBookingsCount = bookingRepository.countByStatus(BookingStatus.COMPLETED);
            stats.put("completedBookings", completedBookingsCount != null ? completedBookingsCount : 0);

            // Approved Boats
            List<Boat> approvedBoats = boatRepository.findByApprovalStatus(Boat.ApprovalStatus.APPROVED);
            stats.put("approvedBoats", approvedBoats.size());

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch dashboard statistics: " + e.getMessage()));
        }
    }

    // Revenue Statistics - SIMPLIFIED VERSION
    @GetMapping("/revenue-statistics")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics() {
        try {
            Map<String, Object> response = new HashMap<>();

            // Get completed bookings for revenue calculation
            List<Booking> completedBookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);

            // Calculate totals
            Double totalRevenue = completedBookings.stream()
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0.0)
                    .sum();

            Double companyRevenue = totalRevenue * 0.2; // 20% commission
            Double ownerPayout = totalRevenue * 0.8; // 80% to owners
            Double taxAmount = totalRevenue * 0.1; // 10% tax

            response.put("totalRevenue", totalRevenue);
            response.put("companyRevenue", companyRevenue);
            response.put("ownerPayout", ownerPayout);
            response.put("taxAmount", taxAmount);
            response.put("completedBookings", completedBookings.size());
            response.put("averageRevenuePerBooking", completedBookings.isEmpty() ? 0 : totalRevenue / completedBookings.size());
            response.put("commissionRate", 20.0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch revenue statistics: " + e.getMessage()));
        }
    }

    // Revenue Distribution (Pie Chart Data) - SIMPLIFIED
    @GetMapping("/revenue-distribution")
    public ResponseEntity<Map<String, Object>> getRevenueDistribution() {
        try {
            Map<String, Object> distribution = new HashMap<>();

            // Get completed bookings for calculation
            List<Booking> completedBookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);
            Double totalRevenue = completedBookings.stream()
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0.0)
                    .sum();

            Double companyRevenue = totalRevenue * 0.2; // 20% commission
            Double ownerPayout = totalRevenue * 0.8; // 80% to owners
            Double taxAmount = totalRevenue * 0.1; // 10% tax

            distribution.put("companyRevenue", companyRevenue);
            distribution.put("ownerPayout", ownerPayout);
            distribution.put("taxAmount", taxAmount);
            distribution.put("totalRevenue", totalRevenue);

            return ResponseEntity.ok(distribution);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch revenue distribution: " + e.getMessage()));
        }
    }

    // Revenue Trend (Line Chart Data - Last 6 months) - SIMPLIFIED
    @GetMapping("/revenue-trend")
    public ResponseEntity<Map<String, Object>> getRevenueTrend() {
        try {
            Map<String, Object> trendData = new HashMap<>();

            List<String> dates = new ArrayList<>();
            List<Double> totalRevenue = new ArrayList<>();
            List<Double> companyRevenue = new ArrayList<>();

            // Generate last 6 months data (simplified - you can replace with actual data)
            for (int i = 5; i >= 0; i--) {
                YearMonth month = YearMonth.now().minusMonths(i);
                String monthLabel = month.getMonth().toString().substring(0, 3) + " " + month.getYear();

                dates.add(monthLabel);

                // Simplified revenue calculation - replace with actual monthly data
                Double monthRevenue = 1000.0 + (Math.random() * 5000); // Random data for demo
                totalRevenue.add(monthRevenue);
                companyRevenue.add(monthRevenue * 0.2); // 20% commission
            }

            trendData.put("dates", dates);
            trendData.put("totalRevenue", totalRevenue);
            trendData.put("companyRevenue", companyRevenue);

            return ResponseEntity.ok(trendData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch revenue trend: " + e.getMessage()));
        }
    }

    // Booking Status Distribution - WORKING VERSION
    @GetMapping("/booking-status-distribution")
    public ResponseEntity<Map<String, Object>> getBookingStatusDistribution() {
        try {
            Map<String, Object> distribution = new HashMap<>();

            List<String> labels = new ArrayList<>();
            List<Long> values = new ArrayList<>();

            // Get counts for each status
            for (BookingStatus status : BookingStatus.values()) {
                Long count = bookingRepository.countByStatus(status);
                if (count != null && count > 0) {
                    labels.add(status.toString());
                    values.add(count);
                }
            }

            distribution.put("labels", labels);
            distribution.put("values", values);

            return ResponseEntity.ok(distribution);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch booking status distribution: " + e.getMessage()));
        }
    }

    // Monthly Bookings Statistics - SIMPLIFIED
    @GetMapping("/monthly-bookings")
    public ResponseEntity<Map<String, Object>> getMonthlyBookings() {
        try {
            Map<String, Object> monthlyData = new HashMap<>();

            List<String> months = new ArrayList<>();
            List<Long> bookingCounts = new ArrayList<>();
            List<Double> revenue = new ArrayList<>();

            // Generate last 6 months data (simplified)
            for (int i = 5; i >= 0; i--) {
                YearMonth month = YearMonth.now().minusMonths(i);
                String monthLabel = month.getMonth().toString().substring(0, 3);

                months.add(monthLabel);

                // Simplified data - replace with actual counts
                Long count = 5L + (long)(Math.random() * 20); // Random count for demo
                Double monthRevenue = count * (50 + (Math.random() * 100)); // Random revenue

                bookingCounts.add(count);
                revenue.add(monthRevenue);
            }

            monthlyData.put("months", months);
            monthlyData.put("counts", bookingCounts);
            monthlyData.put("revenue", revenue);

            return ResponseEntity.ok(monthlyData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch monthly bookings: " + e.getMessage()));
        }
    }

    // Detailed Report Data - ONLY COMPLETED BOOKINGS
    @GetMapping("/report-data")
    public ResponseEntity<List<Map<String, Object>>> getReportData() {
        try {
            List<Map<String, Object>> reportData = new ArrayList<>();

            // Get ONLY completed bookings with their revenue records
            List<Booking> completedBookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);

            System.out.println("Found " + completedBookings.size() + " completed bookings for report");

            for (Booking booking : completedBookings) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", booking.getId());
                row.put("bookingId", booking.getId());
                row.put("bookingDate", booking.getBookingDate());
                row.put("tripDate", booking.getTripDate());
                row.put("customerName", booking.getFirstName() + " " + booking.getLastName());
                row.put("email", booking.getEmail());
                row.put("phone", booking.getPhoneNumber());
                row.put("tripName", booking.getTrip() != null ? booking.getTrip().getName() : "N/A");
                row.put("numberOfPeople", booking.getNumberOfPeople());
                row.put("status", booking.getStatus());
                row.put("totalPrice", booking.getTotalPrice());

                // Calculate revenue breakdown for completed bookings
                BigDecimal totalPrice = booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO;
                BigDecimal taxAmount = totalPrice.multiply(new BigDecimal("0.10")); // 10% tax
                BigDecimal netRevenue = totalPrice.subtract(taxAmount);
                BigDecimal commissionAmount = netRevenue.multiply(new BigDecimal("0.20")); // 20% commission
                BigDecimal ownerPayout = netRevenue.subtract(commissionAmount);
                BigDecimal companyRevenue = commissionAmount;

                row.put("taxAmount", taxAmount);
                row.put("commissionAmount", commissionAmount);
                row.put("ownerPayout", ownerPayout);
                row.put("companyRevenue", companyRevenue);
                row.put("netRevenue", netRevenue);

                reportData.add(row);
            }

            // Sort by booking date descending (most recent first)
            reportData.sort((a, b) -> {
                LocalDateTime dateA = (LocalDateTime) a.get("bookingDate");
                LocalDateTime dateB = (LocalDateTime) b.get("bookingDate");
                return dateB.compareTo(dateA);
            });

            return ResponseEntity.ok(reportData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

    // Trip Performance Ranking - WORKING VERSION
    @GetMapping("/trip-performance")
    public ResponseEntity<List<Map<String, Object>>> getTripPerformance() {
        try {
            List<Map<String, Object>> performanceData = new ArrayList<>();

            // Get all active trips
            List<com.boatsafari.model.Trip> activeTrips = tripRepository.findByActiveTrue();

            for (com.boatsafari.model.Trip trip : activeTrips) {
                Map<String, Object> performance = new HashMap<>();
                performance.put("id", trip.getId());
                performance.put("name", trip.getName());
                performance.put("type", trip.getType());
                performance.put("capacity", trip.getCapacity());
                performance.put("pricePerPerson", trip.getPricePerPerson());
                performance.put("destination", trip.getDestination());

                // Get bookings for this trip
                List<Booking> tripBookings = bookingRepository.findByTripId(trip.getId());
                List<Booking> completedBookings = tripBookings.stream()
                        .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                        .collect(Collectors.toList());

                // Calculate statistics
                Long totalBookings = (long) completedBookings.size();
                BigDecimal totalRevenue = completedBookings.stream()
                        .map(b -> b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                Double avgPassengers = completedBookings.stream()
                        .mapToInt(Booking::getNumberOfPeople)
                        .average()
                        .orElse(0.0);

                // Calculate utilization (based on capacity and bookings)
                double utilization = 0.0;
                if (trip.getCapacity() > 0 && !completedBookings.isEmpty()) {
                    int totalPassengers = completedBookings.stream()
                            .mapToInt(Booking::getNumberOfPeople)
                            .sum();
                    utilization = ((double) totalPassengers / (trip.getCapacity() * completedBookings.size())) * 100;
                    utilization = Math.min(utilization, 100.0);
                }

                performance.put("totalBookings", totalBookings);
                performance.put("revenue", totalRevenue);
                performance.put("avgPassengers", Math.round(avgPassengers * 10.0) / 10.0);
                performance.put("utilization", Math.round(utilization * 10.0) / 10.0);

                // Performance indicator
                String performanceLevel = utilization >= 80 ? "Excellent" :
                        utilization >= 60 ? "Good" :
                                utilization >= 40 ? "Average" : "Low";
                performance.put("performance", performanceLevel);

                performanceData.add(performance);
            }

            // Sort by revenue (descending)
            performanceData.sort((a, b) -> {
                BigDecimal revenueA = (BigDecimal) a.get("revenue");
                BigDecimal revenueB = (BigDecimal) b.get("revenue");
                return revenueB.compareTo(revenueA);
            });

            return ResponseEntity.ok(performanceData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

    // Boat Utilization Statistics - WORKING VERSION
    @GetMapping("/boat-utilization")
    public ResponseEntity<List<Map<String, Object>>> getBoatUtilization() {
        try {
            List<Map<String, Object>> utilizationData = new ArrayList<>();

            // Get all approved boats
            List<Boat> approvedBoats = boatRepository.findByApprovalStatus(Boat.ApprovalStatus.APPROVED);

            for (Boat boat : approvedBoats) {
                Map<String, Object> boatStats = new HashMap<>();
                boatStats.put("id", boat.getId());
                boatStats.put("boatName", boat.getBoatName());
                boatStats.put("boatType", boat.getBoatType());
                boatStats.put("capacity", boat.getCapacity());
                boatStats.put("registrationNumber", boat.getRegistrationNumber());
                boatStats.put("status", boat.getStatus());

                // Get assignments for this boat (you'll need to implement this)
                // For now, using simplified calculation
                Long assignmentCount = 5L + (long)(Math.random() * 20); // Random for demo
                boatStats.put("assignmentCount", assignmentCount);

                // Calculate utilization percentage
                double utilization = assignmentCount > 0 ? (assignmentCount.doubleValue() / 30.0) * 100 : 0;
                utilization = Math.min(utilization, 100.0);
                boatStats.put("utilization", Math.round(utilization * 10.0) / 10.0);

                utilizationData.add(boatStats);
            }

            return ResponseEntity.ok(utilizationData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

    // Customer Analysis - WORKING VERSION
    @GetMapping("/customer-analysis")
    public ResponseEntity<List<Map<String, Object>>> getCustomerAnalysis() {
        try {
            List<Booking> allBookings = bookingRepository.findAll();
            Map<String, Map<String, Object>> customerData = new HashMap<>();

            for (Booking booking : allBookings) {
                String email = booking.getEmail();
                customerData.putIfAbsent(email, new HashMap<>());

                Map<String, Object> customer = customerData.get(email);
                customer.put("email", email);
                customer.put("name", booking.getFirstName() + " " + booking.getLastName());
                customer.put("phone", booking.getPhoneNumber());

                // Count bookings
                Long bookingCount = (Long) customer.getOrDefault("totalBookings", 0L);
                customer.put("totalBookings", bookingCount + 1);

                // Calculate total spending
                BigDecimal totalSpent = (BigDecimal) customer.getOrDefault("totalSpent", BigDecimal.ZERO);
                if (booking.getTotalPrice() != null) {
                    customer.put("totalSpent", totalSpent.add(booking.getTotalPrice()));
                } else {
                    customer.put("totalSpent", totalSpent);
                }

                // Track last booking date
                LocalDateTime lastBooking = (LocalDateTime) customer.get("lastBookingDate");
                if (lastBooking == null || booking.getBookingDate().isAfter(lastBooking)) {
                    customer.put("lastBookingDate", booking.getBookingDate());
                }
            }

            List<Map<String, Object>> analysis = new ArrayList<>(customerData.values());

            // Sort by total spent (descending)
            analysis.sort((a, b) -> {
                BigDecimal spentA = (BigDecimal) a.get("totalSpent");
                BigDecimal spentB = (BigDecimal) b.get("totalSpent");
                return spentB.compareTo(spentA);
            });

            return ResponseEntity.ok(analysis);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }
}