package com.boatsafari.config;

import com.boatsafari.model.Boat;
import com.boatsafari.model.User;
import com.boatsafari.model.UserRole;
import com.boatsafari.repository.BoatRepository;
import com.boatsafari.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create sample boats if none exist
        if (boatRepository.count() == 0) {
            // Get all boat owners using your existing findByRole method
            List<User> boatOwners = userRepository.findByRole(UserRole.BOAT_OWNER);

            if (boatOwners.isEmpty()) {
                System.out.println("No boat owners found in the system. Creating unassigned boats.");
                createUnassignedBoats();
            } else {
                System.out.println("Found " + boatOwners.size() + " boat owner(s). Assigning boats...");

                // Create boats for each boat owner
                int boatCounter = 1;

                for (User owner : boatOwners) {
                    System.out.println("Creating boats for owner: " + owner.getFirstName() + " " + owner.getLastName() + " (" + owner.getEmail() + ")");

                    // Create 2-3 boats per owner
                    int boatsToCreate = 2 + (boatCounter % 2); // Creates 2 or 3 boats alternately

                    for (int i = 0; i < boatsToCreate; i++) {
                        Boat boat = createBoatForOwner(owner, i, boatCounter);
                        boatRepository.save(boat);
                        System.out.println("  - Created: " + boat.getBoatName() + " (" + boat.getBoatType() + ", Capacity: " + boat.getCapacity() + ")");
                        boatCounter++;
                    }
                }

                // Create some additional system boats for backup
                createSystemBoats(boatCounter);

                System.out.println("Sample boats created successfully. Total boats: " + boatRepository.count());
            }
        } else {
            System.out.println("Boats already exist in database. Total boats: " + boatRepository.count());
        }
    }

    private Boat createBoatForOwner(User owner, int index, int boatCounter) {
        String boatName = generateBoatName(owner, index);
        String boatType = generateBoatType(index);
        int capacity = generateCapacity(index);
        String registrationNumber = "REG" + String.format("%03d", boatCounter);

        // UPDATED: Use constructor with ownerId instead of setOwner()
        Boat boat = new Boat(boatName, boatType, capacity, registrationNumber, owner.getId());

        // Set additional boat properties
        boat.setHourlyRate(generateHourlyRate(index));
        boat.setDescription(generateDescription(boatName, boatType, capacity));
        boat.setAmenities(generateAmenities(boatType));
        boat.setApprovalStatus(Boat.ApprovalStatus.APPROVED);

        return boat;
    }

    private String generateBoatName(User owner, int index) {
        String ownerName = owner.getFirstName();
        String[] nameTemplates = {
                ownerName + "'s Explorer",
                ownerName + "'s Voyager",
                ownerName + "'s Cruiser",
                ownerName + "'s Adventurer",
                ownerName + "'s Seeker",
                ownerName + "'s Princess",
                ownerName + "'s Warrior",
                ownerName + "'s Dream"
        };
        return nameTemplates[index % nameTemplates.length];
    }

    private String generateBoatType(int index) {
        String[] types = {
                "Safari Boat", "Speed Boat", "Luxury Yacht", "Fishing Boat",
                "Catamaran", "Pontoon Boat", "Glass Bottom Boat", "House Boat"
        };
        return types[index % types.length];
    }

    private int generateCapacity(int index) {
        int[] capacities = {8, 12, 15, 20, 25, 30, 6, 10};
        return capacities[index % capacities.length];
    }

    private Double generateHourlyRate(int index) {
        double[] rates = {75.0, 100.0, 150.0, 60.0, 120.0, 80.0, 200.0, 90.0};
        return rates[index % rates.length];
    }

    private String generateDescription(String boatName, String boatType, int capacity) {
        return String.format("%s is a %s with capacity for %d passengers. Perfect for various water activities and tours.",
                boatName, boatType.toLowerCase(), capacity);
    }

    private String generateAmenities(String boatType) {
        switch (boatType) {
            case "Safari Boat":
                return "Guide, Binoculars, Refreshments";
            case "Speed Boat":
                return "Life jackets, Safety equipment";
            case "Luxury Yacht":
                return "Bar, Music system, Air conditioning, Sun deck";
            case "Fishing Boat":
                return "Fishing gear, Cooler, Bait";
            case "Catamaran":
                return "Spacious deck, Shaded area, Refreshments";
            case "Glass Bottom Boat":
                return "Glass viewing panels, Underwater lights, Guide";
            default:
                return "Life jackets, Basic amenities";
        }
    }

    private void createUnassignedBoats() {
        String[] boatNames = {
                "River Queen", "Ocean Explorer", "Wildlife Watcher",
                "Sunset Cruiser", "Adventure Seeker", "Blue Dolphin"
        };
        String[] boatTypes = {
                "Safari Boat", "Speed Boat", "Safari Boat",
                "Luxury Yacht", "Fishing Boat", "Catamaran"
        };
        int[] capacities = {12, 8, 15, 20, 6, 25};
        Double[] hourlyRates = {80.0, 110.0, 85.0, 180.0, 65.0, 95.0};

        for (int i = 0; i < boatNames.length; i++) {
            // UPDATED: Use constructor without ownerId for unassigned boats
            Boat boat = new Boat(boatNames[i], boatTypes[i], capacities[i], "SYS" + (i + 1));
            boat.setHourlyRate(hourlyRates[i]);
            boat.setDescription(generateDescription(boatNames[i], boatTypes[i], capacities[i]));
            boat.setAmenities(generateAmenities(boatTypes[i]));
            boat.setApprovalStatus(Boat.ApprovalStatus.APPROVED);

            boatRepository.save(boat);
            System.out.println("Created unassigned boat: " + boatNames[i]);
        }
    }

    private void createSystemBoats(int startCounter) {
        String[] systemBoatNames = {"System Safari", "Emergency Cruiser", "Backup Vessel"};
        String[] systemBoatTypes = {"Safari Boat", "Speed Boat", "Fishing Boat"};
        int[] systemCapacities = {10, 6, 8};
        Double[] systemHourlyRates = {70.0, 90.0, 55.0};

        for (int i = 0; i < systemBoatNames.length; i++) {
            // UPDATED: Use constructor without ownerId for system boats
            Boat boat = new Boat(systemBoatNames[i], systemBoatTypes[i], systemCapacities[i], "SYS" + (startCounter + i));
            boat.setHourlyRate(systemHourlyRates[i]);
            boat.setDescription(generateDescription(systemBoatNames[i], systemBoatTypes[i], systemCapacities[i]));
            boat.setAmenities(generateAmenities(systemBoatTypes[i]));
            boat.setApprovalStatus(Boat.ApprovalStatus.APPROVED);

            boatRepository.save(boat);
            System.out.println("Created system boat: " + systemBoatNames[i]);
        }
    }
}