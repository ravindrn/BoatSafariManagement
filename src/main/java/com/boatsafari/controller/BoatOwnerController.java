package com.boatsafari.controller;

import com.boatsafari.model.Boat;
import com.boatsafari.model.BoatAssignment;
import com.boatsafari.model.BookingRevenue;
import com.boatsafari.service.BoatOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boat-owner")
@CrossOrigin(origins = "*")
public class BoatOwnerController {

    @Autowired
    private BoatOwnerService boatOwnerService;

    // Get all boats owned by the current boat owner
    @GetMapping("/boats")
    public ResponseEntity<List<Boat>> getMyBoats(@RequestHeader("userId") Long ownerId) {
        List<Boat> boats = boatOwnerService.getBoatsByOwner(ownerId);
        return ResponseEntity.ok(boats);
    }

    // Get boat assignments for owner's boats
    @GetMapping("/assignments")
    public ResponseEntity<List<Map<String, Object>>> getMyBoatAssignments(@RequestHeader("userId") Long ownerId) {
        List<Map<String, Object>> assignments = boatOwnerService.getBoatAssignmentsByOwner(ownerId);
        return ResponseEntity.ok(assignments);
    }

    // Get assignments for a specific boat
    @GetMapping("/boats/{boatId}/assignments")
    public ResponseEntity<List<BoatAssignment>> getAssignmentsByBoat(
            @RequestHeader("userId") Long ownerId,
            @PathVariable Long boatId) {
        List<BoatAssignment> assignments = boatOwnerService.getAssignmentsByBoat(ownerId, boatId);
        return ResponseEntity.ok(assignments);
    }

    // Get revenue details for owner's boats
    @GetMapping("/revenue")
    public ResponseEntity<List<BookingRevenue>> getMyRevenue(@RequestHeader("userId") Long ownerId) {
        List<BookingRevenue> revenue = boatOwnerService.getRevenueByOwner(ownerId);
        return ResponseEntity.ok(revenue);
    }

    // Get boat owner statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMyStats(@RequestHeader("userId") Long ownerId) {
        Map<String, Object> stats = boatOwnerService.getBoatOwnerStats(ownerId);
        return ResponseEntity.ok(stats);
    }

    // Add new boat
    @PostMapping("/boats")
    public ResponseEntity<Boat> addBoat(@RequestHeader("userId") Long ownerId, @RequestBody Boat boat) {
        Boat newBoat = boatOwnerService.addBoat(ownerId, boat);
        return ResponseEntity.ok(newBoat);
    }

    // Update boat details
    @PutMapping("/boats/{boatId}")
    public ResponseEntity<Boat> updateBoat(@RequestHeader("userId") Long ownerId,
                                           @PathVariable Long boatId,
                                           @RequestBody Boat boat) {
        Boat updatedBoat = boatOwnerService.updateBoat(ownerId, boatId, boat);
        return ResponseEntity.ok(updatedBoat);
    }

    // Delete boat
    @DeleteMapping("/boats/{boatId}")
    public ResponseEntity<?> deleteBoat(@RequestHeader("userId") Long ownerId,
                                        @PathVariable Long boatId) {
        boatOwnerService.deleteBoat(ownerId, boatId);
        return ResponseEntity.ok().build();
    }
}