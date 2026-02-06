// src/main/java/com/boatsafari/config/DataLoader.java
package com.boatsafari.config;

import com.boatsafari.model.*;
import com.boatsafari.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.boatsafari.model.ReviewStatus;

// OR if you want to use the common enum:
import com.boatsafari.model.ReviewStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DashboardStatsRepository dashboardStatsRepository;

    @Autowired
    private HeroContentRepository heroContentRepository;

    @Autowired
    private AboutContentRepository aboutContentRepository;

    @Autowired
    private AboutStatsRepository aboutStatsRepository;

    @Autowired
    private AboutFeaturesRepository aboutFeaturesRepository;

    @Autowired
    private NewReviewRepository newReviewRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create demo users with proper password encoding

       

        // Create sample trips if none exist
        if (tripRepository.count() == 0) {
            Trip trip1 = new Trip("Amazon Wildlife Safari", "Wildlife Safari",
                    "Explore the diverse wildlife of the Amazon River with our expert guides.",
                    3, 25, new BigDecimal("75.00"), "Amazon River");
            trip1.setImageUrl("https://images.unsplash.com/photo-1594736797933-d0e013c8c7e7?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80");
            trip1.setFeatured(true);

            Trip trip2 = new Trip("Nile Sunset Cruise", "Sunset Cruise",
                    "Enjoy a breathtaking sunset while cruising along the historic Nile River.",
                    2, 30, new BigDecimal("65.00"), "Nile River");
            trip2.setImageUrl("https://images.unsplash.com/photo-1578662996442-48f60103fc96?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80");
            trip2.setFeatured(true);

            Trip trip3 = new Trip("Mississippi Bird Watching", "Bird Watching",
                    "Perfect for bird enthusiasts! Spot various species in their natural habitat.",
                    4, 20, new BigDecimal("55.00"), "Mississippi River");
            trip3.setImageUrl("https://images.unsplash.com/photo-1501426026826-31c667bdf23d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80");
            trip3.setFeatured(true);

            tripRepository.saveAll(Arrays.asList(trip1, trip2, trip3));
            System.out.println("Sample trips created");
        }

        // Create sample gallery items
        // (Your existing gallery creation code here)

        // Create sample reviews
        if (reviewRepository.count() == 0 && tripRepository.count() > 0) {
            Trip trip1 = tripRepository.findAll().get(0);
            Trip trip2 = tripRepository.findAll().get(1);

            Review review1 = new Review("Sarah Johnson", 5,
                    "The Amazon Wildlife Safari was absolutely incredible! Our guide was knowledgeable and we spotted so many exotic animals. Highly recommend this experience!",
                    trip1);
            review1.setApproved(true);

            Review review2 = new Review("Michael Brown", 4,
                    "The sunset cruise on the Nile was magical. The colors, the scenery, and the comfortable boat made for a perfect evening. Will definitely book again!",
                    trip2);
            review2.setApproved(true);

            reviewRepository.saveAll(Arrays.asList(review1, review2));
            System.out.println("Sample reviews created");
        }

        // Create dashboard stats
        if (dashboardStatsRepository.count() == 0) {
            DashboardStats stat1 = new DashboardStats("happy_customers", "500+");
            DashboardStats stat2 = new DashboardStats("trip_destinations", "50+");
            DashboardStats stat3 = new DashboardStats("satisfaction_rate", "98%");
            DashboardStats stat4 = new DashboardStats("rated_service", "5★");

            dashboardStatsRepository.saveAll(Arrays.asList(stat1, stat2, stat3, stat4));
            System.out.println("Dashboard stats created");
        }

        if (heroContentRepository.count() == 0) {
            HeroContent heroContent = new HeroContent(
                    "main_hero",
                    "Experience the Ultimate Boat Safari Adventure",
                    "Discover breathtaking rivers, exotic wildlife, and unforgettable experiences with our premium boat safari trips. Book your adventure today!"
            );
            heroContentRepository.save(heroContent);
            System.out.println("Default hero content created");
        }

        if (aboutContentRepository.count() == 0) {
            AboutContent aboutContent = new AboutContent(
                    "main_about",
                    "About Boat Safari",
                    "Your Premier Boat Safari Experience",
                    "Founded in 2010, Boat Safari has been providing unforgettable river exploration experiences to adventure seekers and nature lovers from around the world. Our mission is to connect people with nature through safe, educational, and thrilling boat safari adventures.",
                    "We operate a fleet of modern, well-maintained boats and employ experienced guides who are passionate about wildlife and conservation. Our trips are designed to showcase the beauty of river ecosystems while promoting sustainable tourism practices."
            );
            aboutContentRepository.save(aboutContent);
            System.out.println("Default about content created");
        }

        // Create about statistics if none exist
        if (aboutStatsRepository.count() == 0) {
            AboutStats stat1 = new AboutStats("years_experience", "13+", "Years Experience");
            AboutStats stat2 = new AboutStats("expert_guides", "12", "Expert Guides");
            AboutStats stat3 = new AboutStats("modern_boats", "8", "Modern Boats");

            aboutStatsRepository.saveAll(Arrays.asList(stat1, stat2, stat3));
            System.out.println("Default about statistics created");
        }

        // Create about features if none exist
        if (aboutFeaturesRepository.count() == 0) {
            AboutFeatures feature1 = new AboutFeatures("fas fa-shield-alt", "Safety First", "All our boats meet international safety standards", "text-success", 1);
            AboutFeatures feature2 = new AboutFeatures("fas fa-graduation-cap", "Expert Guides", "Certified naturalists and wildlife experts", "text-info", 2);
            AboutFeatures feature3 = new AboutFeatures("fas fa-leaf", "Eco-Friendly", "Committed to sustainable tourism practices", "text-success", 3);
            AboutFeatures feature4 = new AboutFeatures("fas fa-camera", "Photo Opportunities", "Best spots for wildlife photography", "text-primary", 4);
            AboutFeatures feature5 = new AboutFeatures("fas fa-utensils", "Refreshments Included", "Complimentary snacks and drinks", "text-warning", 5);

            aboutFeaturesRepository.saveAll(Arrays.asList(feature1, feature2, feature3, feature4, feature5));
            System.out.println("Default about features created");
        }

        // Create sample new reviews if none exist
        if (newReviewRepository.count() == 0) {
            NewReview review1 = new NewReview("Sarah Johnson", "Amazon Wildlife Safari", 5,
                    "The Amazon Wildlife Safari was absolutely incredible! Our guide was knowledgeable and we spotted so many exotic animals. Highly recommend this experience!");
            review1.setStatus(ReviewStatus.APPROVED);

            NewReview review2 = new NewReview("Michael Brown", "Nile Sunset Cruise", 4,
                    "The sunset cruise on the Nile was magical. The colors, the scenery, and the comfortable boat made for a perfect evening. Will definitely book again!");
            review2.setStatus(ReviewStatus.APPROVED);

            NewReview review3 = new NewReview("Emma Davis", "Mississippi Bird Watching", 5,
                    "As a bird enthusiast, the Mississippi Bird Watching tour exceeded my expectations. We saw rare species I've only read about in books. Fantastic!");
            review3.setStatus(ReviewStatus.APPROVED);

            newReviewRepository.saveAll(Arrays.asList(review1, review2, review3));
            System.out.println("Sample new reviews created");
        }
    }

    private void createDemoUser(String username, String email, String password, String firstName, String lastName, UserRole role) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole(role);
            user.setCreatedBy("SYSTEM_ADMIN"); // Mark as created by admin
            userRepository.save(user);
            System.out.println("Created demo user: " + email + " with role: " + role);
        }
    }
}