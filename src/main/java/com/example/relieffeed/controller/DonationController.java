package com.example.relieffeed.controller;

import com.example.relieffeed.model.Donation;
import com.example.relieffeed.service.DonationService;
import com.example.relieffeed.service.ImageHandlingService;
import com.example.relieffeed.service.ImpactService;
import com.example.relieffeed.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*") // For hackathon demo
public class DonationController {

    private final DonationService donationService;
    private final LocationService locationService;
    private final ImpactService impactService;
    private final ImageHandlingService imageHandlingService;

    public DonationController(DonationService donationService, LocationService locationService, 
                              ImpactService impactService, ImageHandlingService imageHandlingService) {
        this.donationService = donationService;
        this.locationService = locationService;
        this.impactService = impactService;
        this.imageHandlingService = imageHandlingService;
    }

    // Donor: Post food
    @PostMapping
    public ResponseEntity<Donation> postDonation(@RequestBody Donation donation) {
        return ResponseEntity.ok(donationService.postDonation(donation));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Donation>> getAllDonations() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }

    // Volunteer: Find Nearby
    @GetMapping("/nearby")
    public ResponseEntity<List<Donation>> getNearbyDonations(@RequestParam Double lat, @RequestParam Double lng) {
        List<Donation> nearby = locationService.findNearbyDonations(lat, lng);
        return ResponseEntity.ok(nearby);
    }

    // Volunteer: Claim
    @PostMapping("/{id}/claim")
    public ResponseEntity<Donation> claimDonation(@PathVariable Long id, @RequestParam String volunteerName) {
        return ResponseEntity.ok(donationService.claimDonation(id, volunteerName));
    }

    // Volunteer: Verify OTP / Pickup
    @PostMapping("/{id}/pickup")
    public ResponseEntity<Donation> pickupDonation(@PathVariable Long id, @RequestParam String otp) {
        return ResponseEntity.ok(donationService.pickupDonation(id, otp));
    }

    // Volunteer: Mark Distributed
    @PostMapping("/{id}/distribute")
    public ResponseEntity<Donation> markDistributed(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.markDistributed(id));
    }

    // Admin: Summary of total meals saved
    @GetMapping("/impact")
    public ResponseEntity<Map<String, Object>> getImpactSummary() {
        int totalMealsSaved = impactService.getTotalMealsSaved();
        return ResponseEntity.ok(Map.of(
            "totalMealsSaved", totalMealsSaved
        ));
    }

    // Donor: Get donation history
    @GetMapping("/donor/{donorName}")
    public ResponseEntity<List<Donation>> getDonorHistory(@PathVariable String donorName) {
        return ResponseEntity.ok(donationService.getDonationsByDonor(donorName));
    }

    // Volunteer: Get history
    @GetMapping("/volunteer/{volunteerName}")
    public ResponseEntity<List<Donation>> getVolunteerHistory(@PathVariable String volunteerName) {
        return ResponseEntity.ok(donationService.getDonationsByVolunteer(volunteerName));
    }

    // Admin: Top Donors based on meals saved
    @GetMapping("/impact/top-donors")
    public ResponseEntity<List<Map<String, Object>>> getTopDonors() {
        return ResponseEntity.ok(impactService.getTopDonors());
    }

    // Admin: Active donations for map data
    @GetMapping("/active")
    public ResponseEntity<List<Donation>> getActiveDonations() {
        return ResponseEntity.ok(donationService.getActiveDonations());
    }

    // Image Upload Stub
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = imageHandlingService.uploadImage(file);
        return ResponseEntity.ok(Map.of("imageUrl", url));
    }
}
