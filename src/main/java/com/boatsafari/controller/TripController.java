package com.boatsafari.controller;

import com.boatsafari.model.Trip;
import com.boatsafari.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class TripController {

    @Autowired
    private TripService tripService;

    @GetMapping
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/active")
    public List<Trip> getActiveTrips() {
        return tripService.getActiveTrips();
    }

    @GetMapping("/featured")
    public List<Trip> getFeaturedTrips() {
        return tripService.getFeaturedTrips();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
        Optional<Trip> trip = tripService.getTripById(id);
        return trip.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Map<String, Object>> getTripAvailability(@PathVariable Long id) {
        try {
            Trip trip = tripService.getTripById(id)
                    .orElseThrow(() -> new RuntimeException("Trip not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("tripId", trip.getId());
            response.put("name", trip.getName());
            response.put("totalCapacity", trip.getCapacity());
            response.put("pricePerPerson", trip.getPricePerPerson());
            response.put("available", trip.isActive());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getTripTypes() {
        List<String> types = tripService.getAllTrips().stream()
                .map(Trip::getType)
                .distinct()
                .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }

    @PostMapping
    public Trip createTrip(@RequestBody Trip trip) {
        return tripService.createTrip(trip);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable Long id, @RequestBody Trip tripDetails) {
        try {
            Trip updatedTrip = tripService.updateTrip(id, tripDetails);
            return ResponseEntity.ok(updatedTrip);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        try {
            tripService.deleteTrip(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}