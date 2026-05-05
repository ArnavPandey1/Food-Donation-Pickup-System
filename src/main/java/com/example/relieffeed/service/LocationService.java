package com.example.relieffeed.service;

import com.example.relieffeed.model.Donation;
import com.example.relieffeed.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final DonationRepository donationRepository;
    
    @Value("${relief.feed.search-radius-km:5.0}")
    private Double defaultRadiusKm;

    public LocationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public List<Donation> findNearbyDonations(Double lat, Double lng) {
        return donationRepository.findNearbyAvailableDonations(lat, lng, defaultRadiusKm);
    }
}
