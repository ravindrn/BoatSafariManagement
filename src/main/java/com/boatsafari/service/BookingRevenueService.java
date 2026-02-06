package com.boatsafari.service;

import com.boatsafari.model.Booking;
import com.boatsafari.model.BookingRevenue;
import com.boatsafari.model.BoatAssignment;
import com.boatsafari.repository.BookingRevenueRepository;
import com.boatsafari.repository.BoatAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingRevenueService {

    @Autowired
    private BookingRevenueRepository bookingRevenueRepository;

    @Autowired
    private BoatAssignmentRepository boatAssignmentRepository;

    // Commission rate (20% for company)
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.20");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% tax

    // Create revenue record when booking is completed
    public BookingRevenue createRevenueRecord(Booking booking) {
        // Check if revenue record already exists
        Optional<BookingRevenue> existingRevenue = bookingRevenueRepository.findByBookingId(booking.getId());
        if (existingRevenue.isPresent()) {
            return existingRevenue.get();
        }

        // Get boat assignment for this booking
        Optional<BoatAssignment> assignment = boatAssignmentRepository.findByBookingId(booking.getId());

        // FIXED: Make boat assignment optional since it might not exist yet
        // We can still create revenue record without boat assignment
        // but we'll need to handle owner-specific calculations differently

        BookingRevenue revenue = new BookingRevenue();
        revenue.setBookingId(booking.getId());

        // Calculate revenue with commission
        BigDecimal tripRevenue = booking.getTotalPrice() != null ?
                booking.getTotalPrice() : BigDecimal.ZERO; // FIXED: Direct assignment

        // Calculate tax (10% of trip revenue)
        BigDecimal taxAmount = tripRevenue.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);

        // Calculate net revenue (trip revenue - tax)
        BigDecimal netRevenue = tripRevenue.subtract(taxAmount).setScale(2, RoundingMode.HALF_UP);

        // Calculate commission (20% of net revenue)
        BigDecimal commissionAmount = netRevenue.multiply(COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);

        // Calculate owner payout (net revenue - commission)
        BigDecimal ownerPayout = netRevenue.subtract(commissionAmount).setScale(2, RoundingMode.HALF_UP);

        // Company revenue is the commission amount
        BigDecimal companyRevenue = commissionAmount;

        revenue.setTripRevenue(tripRevenue);
        revenue.setTaxAmount(taxAmount);
        revenue.setNetRevenue(netRevenue);
        revenue.setCommissionRate(COMMISSION_RATE);
        revenue.setCommissionAmount(commissionAmount);
        revenue.setOwnerPayout(ownerPayout);
        revenue.setCompanyRevenue(companyRevenue);
        revenue.setRevenueStatus(BookingRevenue.RevenueStatus.PAID);
        revenue.setPaymentDate(LocalDateTime.now());

        return bookingRevenueRepository.save(revenue);
    }

    // Get revenue by booking ID
    public Optional<BookingRevenue> getRevenueByBookingId(Long bookingId) {
        return bookingRevenueRepository.findByBookingId(bookingId);
    }

    // Get revenue by boat owner - FIXED: Handle cases where booking revenue might not exist
    public List<BookingRevenue> getRevenueByBoatOwner(Long ownerId) {
        // Get all boat assignments for this owner
        List<BoatAssignment> assignments = boatAssignmentRepository.findByBoatOwnerId(ownerId);

        return assignments.stream()
                .map(assignment -> bookingRevenueRepository.findByBookingId(assignment.getBooking().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    // Get total revenue statistics for boat owner - FIXED: Handle null values
    public OwnerRevenueStats getOwnerRevenueStats(Long ownerId) {
        List<BookingRevenue> ownerRevenues = getRevenueByBoatOwner(ownerId);

        BigDecimal totalRevenue = ownerRevenues.stream()
                .map(BookingRevenue::getTripRevenue)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOwnerPayout = ownerRevenues.stream()
                .map(BookingRevenue::getOwnerPayout)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommission = ownerRevenues.stream()
                .map(BookingRevenue::getCommissionAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OwnerRevenueStats(
                totalRevenue,
                totalOwnerPayout,
                totalCommission,
                ownerRevenues.size()
        );
    }

    // Get company revenue statistics - FIXED: Handle null values and add null checks
    public CompanyRevenueStats getCompanyRevenueStats(LocalDateTime startDate, LocalDateTime endDate) {
        List<BookingRevenue> allRevenues;

        if (startDate != null && endDate != null) {
            allRevenues = bookingRevenueRepository.findByPaymentDateBetween(startDate, endDate);
        } else {
            allRevenues = bookingRevenueRepository.findAll();
        }

        BigDecimal totalRevenue = allRevenues.stream()
                .map(BookingRevenue::getTripRevenue)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCompanyRevenue = allRevenues.stream()
                .map(BookingRevenue::getCompanyRevenue)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOwnerPayout = allRevenues.stream()
                .map(BookingRevenue::getOwnerPayout)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int completedBookings = allRevenues.size();
        BigDecimal averageRevenue = completedBookings > 0 ?
                totalRevenue.divide(new BigDecimal(completedBookings), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        return new CompanyRevenueStats(
                totalRevenue,
                totalCompanyRevenue,
                totalOwnerPayout,
                completedBookings,
                averageRevenue,
                COMMISSION_RATE.multiply(new BigDecimal("100")) // Convert to percentage
        );
    }

    // Get all revenue records
    public List<BookingRevenue> getAllRevenueRecords() {
        return bookingRevenueRepository.findAll();
    }

    // Update revenue status
    public BookingRevenue updateRevenueStatus(Long bookingId, BookingRevenue.RevenueStatus status) {
        BookingRevenue revenue = bookingRevenueRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Revenue record not found for booking: " + bookingId));

        revenue.setRevenueStatus(status);
        revenue.setUpdatedAt(LocalDateTime.now());

        return bookingRevenueRepository.save(revenue);
    }

    // Calculate daily revenue
    public BigDecimal getDailyRevenue(LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

        List<BookingRevenue> dailyRevenues = bookingRevenueRepository.findByPaymentDateBetween(startOfDay, endOfDay);

        return dailyRevenues.stream()
                .map(BookingRevenue::getTripRevenue)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // DTO for owner revenue stats
    public static class OwnerRevenueStats {
        private final BigDecimal totalRevenue;
        private final BigDecimal totalOwnerPayout;
        private final BigDecimal totalCommission;
        private final int completedBookings;

        public OwnerRevenueStats(BigDecimal totalRevenue, BigDecimal totalOwnerPayout,
                                 BigDecimal totalCommission, int completedBookings) {
            this.totalRevenue = totalRevenue;
            this.totalOwnerPayout = totalOwnerPayout;
            this.totalCommission = totalCommission;
            this.completedBookings = completedBookings;
        }

        // Getters
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public BigDecimal getTotalOwnerPayout() { return totalOwnerPayout; }
        public BigDecimal getTotalCommission() { return totalCommission; }
        public int getCompletedBookings() { return completedBookings; }
    }

    // DTO for company revenue stats
    public static class CompanyRevenueStats {
        private final BigDecimal totalRevenue;
        private final BigDecimal companyRevenue;
        private final BigDecimal ownerPayout;
        private final int completedBookings;
        private final BigDecimal averageRevenuePerBooking;
        private final BigDecimal commissionRate;

        public CompanyRevenueStats(BigDecimal totalRevenue, BigDecimal companyRevenue,
                                   BigDecimal ownerPayout, int completedBookings,
                                   BigDecimal averageRevenuePerBooking, BigDecimal commissionRate) {
            this.totalRevenue = totalRevenue;
            this.companyRevenue = companyRevenue;
            this.ownerPayout = ownerPayout;
            this.completedBookings = completedBookings;
            this.averageRevenuePerBooking = averageRevenuePerBooking;
            this.commissionRate = commissionRate;
        }

        // Getters
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public BigDecimal getCompanyRevenue() { return companyRevenue; }
        public BigDecimal getOwnerPayout() { return ownerPayout; }
        public int getCompletedBookings() { return completedBookings; }
        public BigDecimal getAverageRevenuePerBooking() { return averageRevenuePerBooking; }
        public BigDecimal getCommissionRate() { return commissionRate; }
    }
}