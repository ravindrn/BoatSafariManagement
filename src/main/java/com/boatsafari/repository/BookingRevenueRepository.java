package com.boatsafari.repository;

import com.boatsafari.model.BookingRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRevenueRepository extends JpaRepository<BookingRevenue, Long> {

    // EXISTING METHODS - PRESERVED
    Optional<BookingRevenue> findByBookingId(Long bookingId);
    List<BookingRevenue> findByRevenueStatus(BookingRevenue.RevenueStatus revenueStatus);

    @Query("SELECT br FROM BookingRevenue br WHERE br.paymentDate BETWEEN :startDate AND :endDate")
    List<BookingRevenue> findByPaymentDateBetween(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(br) FROM BookingRevenue br WHERE br.revenueStatus = :status")
    Long countByRevenueStatus(@Param("status") BookingRevenue.RevenueStatus status);

    @Query("SELECT COUNT(br) FROM BookingRevenue br WHERE br.revenueStatus = com.boatsafari.model.BookingRevenue.RevenueStatus.PAID")
    Long countPaidRevenues();

    // Additional useful queries
    @Query("SELECT br FROM BookingRevenue br WHERE br.revenueStatus = :status AND br.paymentDate BETWEEN :startDate AND :endDate")
    List<BookingRevenue> findByRevenueStatusAndPaymentDateBetween(
            @Param("status") BookingRevenue.RevenueStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<BookingRevenue> findByPaymentDateIsNotNull();
    List<BookingRevenue> findByRevenueStatusOrderByPaymentDateDesc(BookingRevenue.RevenueStatus revenueStatus);
    List<BookingRevenue> findAllByOrderByPaymentDateDesc();

    // NEW METHODS FOR OPERATIONS PORTAL - WITH COALESCE FOR NULL SAFETY
    @Query("SELECT COALESCE(SUM(br.tripRevenue), 0) FROM BookingRevenue br")
    Double getTotalRevenue();

    @Query("SELECT COALESCE(SUM(br.companyRevenue), 0) FROM BookingRevenue br")
    Double getTotalCompanyRevenue();

    @Query("SELECT COALESCE(SUM(br.ownerPayout), 0) FROM BookingRevenue br")
    Double getTotalOwnerPayout();

    @Query("SELECT COALESCE(SUM(br.tripRevenue), 0) FROM BookingRevenue br WHERE br.paymentDate BETWEEN :start AND :end")
    Double getTotalRevenueByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT YEAR(br.paymentDate) as year, MONTH(br.paymentDate) as month, COALESCE(SUM(br.tripRevenue), 0) as revenue " +
            "FROM BookingRevenue br WHERE br.paymentDate IS NOT NULL GROUP BY YEAR(br.paymentDate), MONTH(br.paymentDate) " +
            "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyRevenueBreakdown();
}