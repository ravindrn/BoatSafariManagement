package com.boatsafari.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_revenue")
public class BookingRevenue {

    public enum RevenueStatus {
        PENDING, PAID, PROCESSING, FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "trip_revenue", precision = 10, scale = 2, nullable = false)
    private BigDecimal tripRevenue;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "net_revenue", precision = 10, scale = 2, nullable = false)
    private BigDecimal netRevenue;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "commission_amount", precision = 10, scale = 2)
    private BigDecimal commissionAmount = BigDecimal.ZERO;

    @Column(name = "owner_payout", precision = 10, scale = 2)
    private BigDecimal ownerPayout = BigDecimal.ZERO;

    @Column(name = "company_revenue", precision = 10, scale = 2)
    private BigDecimal companyRevenue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "revenue_status", nullable = false)
    private RevenueStatus revenueStatus = RevenueStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payout_date")
    private LocalDateTime payoutDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public BookingRevenue() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public BookingRevenue(Long bookingId, BigDecimal tripRevenue, BigDecimal netRevenue) {
        this();
        this.bookingId = bookingId;
        this.tripRevenue = tripRevenue;
        this.netRevenue = netRevenue;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public BigDecimal getTripRevenue() { return tripRevenue; }
    public void setTripRevenue(BigDecimal tripRevenue) { this.tripRevenue = tripRevenue; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getNetRevenue() { return netRevenue; }
    public void setNetRevenue(BigDecimal netRevenue) { this.netRevenue = netRevenue; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public BigDecimal getOwnerPayout() { return ownerPayout; }
    public void setOwnerPayout(BigDecimal ownerPayout) { this.ownerPayout = ownerPayout; }

    public BigDecimal getCompanyRevenue() { return companyRevenue; }
    public void setCompanyRevenue(BigDecimal companyRevenue) { this.companyRevenue = companyRevenue; }

    public RevenueStatus getRevenueStatus() { return revenueStatus; }
    public void setRevenueStatus(RevenueStatus revenueStatus) { this.revenueStatus = revenueStatus; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public LocalDateTime getPayoutDate() { return payoutDate; }
    public void setPayoutDate(LocalDateTime payoutDate) { this.payoutDate = payoutDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}