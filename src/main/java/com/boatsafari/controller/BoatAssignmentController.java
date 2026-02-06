package com.boatsafari.controller;

import com.boatsafari.dto.BoatAssignmentRequestDTO;
import com.boatsafari.dto.BoatAssignmentResponseDTO;
import com.boatsafari.model.BoatAssignment;
import com.boatsafari.service.BoatAssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/boat-assignments")
@CrossOrigin(origins = "*")
public class BoatAssignmentController {

    private static final Logger logger = LoggerFactory.getLogger(BoatAssignmentController.class);

    @Autowired
    private BoatAssignmentService boatAssignmentService;

    // GET all assignments
    @GetMapping
    public ResponseEntity<List<BoatAssignmentResponseDTO>> getAllAssignments() {
        try {
            List<BoatAssignmentResponseDTO> assignments = boatAssignmentService.convertToDTOList(
                    boatAssignmentService.getAllAssignments());
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            logger.error("Error getting all boat assignments:", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET assignment by ID
    @GetMapping("/{id}")
    public ResponseEntity<BoatAssignmentResponseDTO> getAssignmentById(@PathVariable Long id) {
        try {
            Optional<BoatAssignment> assignment = boatAssignmentService.getAssignmentById(id);
            if (assignment.isPresent()) {
                BoatAssignmentResponseDTO assignmentDTO = boatAssignmentService.convertToDTO(assignment.get());
                return ResponseEntity.ok(assignmentDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting boat assignment by ID {}:", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET assignment by booking ID
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<BoatAssignmentResponseDTO> getAssignmentByBookingId(@PathVariable Long bookingId) {
        try {
            Optional<BoatAssignment> assignment = boatAssignmentService.getAssignmentByBookingId(bookingId);
            if (assignment.isPresent()) {
                BoatAssignmentResponseDTO assignmentDTO = boatAssignmentService.convertToDTO(assignment.get());
                return ResponseEntity.ok(assignmentDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting boat assignment by booking ID {}:", bookingId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // POST - Create new assignment (SIMPLIFIED - no session needed)
    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody BoatAssignmentRequestDTO assignmentRequest) {
        try {
            logger.info("Creating boat assignment for booking: {}", assignmentRequest.getBookingId());

            // No staff ID needed - boat assignment doesn't require knowing which staff member did it
            BoatAssignment assignment = boatAssignmentService.createAssignment(assignmentRequest, null);
            BoatAssignmentResponseDTO assignmentDTO = boatAssignmentService.convertToDTO(assignment);
            return ResponseEntity.ok(assignmentDTO);

        } catch (RuntimeException e) {
            logger.error("Error creating boat assignment:", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error creating boat assignment:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // PUT - Update assignment (SIMPLIFIED - no session needed)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAssignment(@PathVariable Long id,
                                              @RequestBody BoatAssignmentRequestDTO assignmentRequest) {
        try {
            logger.info("Updating boat assignment: {}", id);

            // No staff ID needed
            BoatAssignment assignment = boatAssignmentService.updateAssignment(id, assignmentRequest, null);
            BoatAssignmentResponseDTO assignmentDTO = boatAssignmentService.convertToDTO(assignment);
            return ResponseEntity.ok(assignmentDTO);

        } catch (RuntimeException e) {
            logger.error("Error updating boat assignment {}:", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating boat assignment {}:", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // DELETE assignment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id) {
        try {
            logger.info("Deleting boat assignment: {}", id);

            boatAssignmentService.deleteAssignment(id);
            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            logger.error("Error deleting boat assignment {}:", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error deleting boat assignment {}:", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}