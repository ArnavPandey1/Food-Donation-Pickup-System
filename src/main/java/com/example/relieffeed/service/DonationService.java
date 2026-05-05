package com.example.relieffeed.service;

import com.example.relieffeed.model.Donation;
import com.example.relieffeed.model.DonationStatus;
import com.example.relieffeed.repository.DonationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final OTPService otpService;
    private final SimpMessagingTemplate messagingTemplate;

    public DonationService(DonationRepository donationRepository, OTPService otpService, SimpMessagingTemplate messagingTemplate) {
        this.donationRepository = donationRepository;
        this.otpService = otpService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Donation postDonation(Donation donation) {
        donation.setStatus(DonationStatus.AVAILABLE);
        donation.setOtp(otpService.generateOTP());
        Donation saved = donationRepository.save(donation);
        
        // Broadcast to WebSocket topic
        messagingTemplate.convertAndSend("/topic/nearby-donations", saved);
        
        return saved;
    }

    @Transactional
    public Donation claimDonation(Long donationId, String volunteerName) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw new RuntimeException("Donation is not available for claiming");
        }

        donation.setStatus(DonationStatus.CLAIMED);
        donation.setVolunteerName(volunteerName);
        return donationRepository.save(donation); // Optimistic locking will handle concurrency
    }

    @Transactional
    public Donation pickupDonation(Long donationId, String otp) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        if (donation.getStatus() != DonationStatus.CLAIMED) {
            throw new RuntimeException("Donation is not claimed");
        }

        if (donation.getOtp() == null || !donation.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        donation.setStatus(DonationStatus.PICKED_UP);
        return donationRepository.save(donation);
    }

    @Transactional
    public Donation markDistributed(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        if (donation.getStatus() != DonationStatus.PICKED_UP) {
            throw new RuntimeException("Donation is not picked up yet");
        }

        donation.setStatus(DonationStatus.DISTRIBUTED);
        return donationRepository.save(donation);
    }

    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    @Transactional
    public void checkExpiredDonations() {
        List<Donation> availableDonations = donationRepository.findByStatus(DonationStatus.AVAILABLE);
        LocalDateTime now = LocalDateTime.now();
        for (Donation donation : availableDonations) {
            if (donation.getExpiryTimestamp() != null && donation.getExpiryTimestamp().isBefore(now)) {
                donation.setStatus(DonationStatus.EXPIRED);
                donationRepository.save(donation);
            }
        }
    }
    
    public List<Donation> getActiveDonations() {
        return donationRepository.findByStatus(DonationStatus.AVAILABLE);
    }
    
    public List<Donation> getDonationsByDonor(String donorName) {
        return donationRepository.findByDonorName(donorName);
    }

    public List<Donation> getDonationsByVolunteer(String volunteerName) {
        return donationRepository.findByVolunteerName(volunteerName);
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }
}
