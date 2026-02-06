package com.boatsafari.dto;

import java.time.LocalDateTime;

public class BookingUpdateDTO {
    private Integer numberOfPeople;
    private LocalDateTime tripDate;
    private String specialRequests;

    // Constructors
    public BookingUpdateDTO() {}

    public BookingUpdateDTO(Integer numberOfPeople, LocalDateTime tripDate, String specialRequests) {
        this.numberOfPeople = numberOfPeople;
        this.tripDate = tripDate;
        this.specialRequests = specialRequests;
    }

    // Getters and Setters
    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public LocalDateTime getTripDate() {
        return tripDate;
    }

    public void setTripDate(LocalDateTime tripDate) {
        this.tripDate = tripDate;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    @Override
    public String toString() {
        return "BookingUpdateDTO{" +
                "numberOfPeople=" + numberOfPeople +
                ", tripDate=" + tripDate +
                ", specialRequests='" + specialRequests + '\'' +
                '}';
    }


}