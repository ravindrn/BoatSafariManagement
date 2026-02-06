package com.boatsafari.dto;

public class BoatAssignmentRequestDTO {
    private Long bookingId;
    private Long boatId;
    private String notes;

    // Constructors
    public BoatAssignmentRequestDTO() {}

    public BoatAssignmentRequestDTO(Long bookingId, Long boatId, String notes) {
        this.bookingId = bookingId;
        this.boatId = boatId;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getBoatId() { return boatId; }
    public void setBoatId(Long boatId) { this.boatId = boatId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "BoatAssignmentRequestDTO{" +
                "bookingId=" + bookingId +
                ", boatId=" + boatId +
                ", notes='" + notes + '\'' +
                '}';
    }
}