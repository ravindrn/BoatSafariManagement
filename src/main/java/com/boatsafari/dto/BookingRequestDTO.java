package com.boatsafari.dto;

import java.time.LocalDateTime;

public class BookingRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Long tripId;
    private Integer numberOfPeople;
    private String specialRequests;
    private LocalDateTime tripDate;

    // Constructors
    public BookingRequestDTO() {}

    public BookingRequestDTO(String firstName, String lastName, String email, String phoneNumber,
                             String address, Long tripId, Integer numberOfPeople, String specialRequests) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.tripId = tripId;
        this.numberOfPeople = numberOfPeople;
        this.specialRequests = specialRequests;
        this.tripDate = LocalDateTime.now().plusDays(1);
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName.trim() : null;
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : null;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = address != null ? address.trim() : null;
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public Integer getNumberOfPeople() { return numberOfPeople; }
    public void setNumberOfPeople(Integer numberOfPeople) { this.numberOfPeople = numberOfPeople; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests != null ? specialRequests.trim() : null;
    }

    public LocalDateTime getTripDate() { return tripDate; }
    public void setTripDate(LocalDateTime tripDate) { this.tripDate = tripDate; }

    @Override
    public String toString() {
        return "BookingRequestDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", tripId=" + tripId +
                ", numberOfPeople=" + numberOfPeople +
                ", specialRequests='" + specialRequests + '\'' +
                ", tripDate=" + tripDate +
                '}';
    }
}