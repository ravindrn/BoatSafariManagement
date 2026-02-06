package com.boatsafari.service;

import com.boatsafari.model.Trip;
import com.boatsafari.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public List<Trip> getActiveTrips() {
        return tripRepository.findByActiveTrue();
    }

    public List<Trip> getFeaturedTrips() {
        return tripRepository.findByFeaturedTrueAndActiveTrue();
    }

    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findById(id);
    }

    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    public Trip updateTrip(Long id, Trip tripDetails) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

        trip.setName(tripDetails.getName());
        trip.setType(tripDetails.getType());
        trip.setDescription(tripDetails.getDescription());
        trip.setDuration(tripDetails.getDuration());
        trip.setCapacity(tripDetails.getCapacity());
        trip.setPricePerPerson(tripDetails.getPricePerPerson());
        trip.setDestination(tripDetails.getDestination());
        trip.setImageUrl(tripDetails.getImageUrl());
        trip.setFeatured(tripDetails.isFeatured());
        trip.setActive(tripDetails.isActive());

        return tripRepository.save(trip);
    }

    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }
}