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

    /**
     * Compute great-circle distance between two points using the Haversine formula (kilometers).
     */
    public static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Helper type pairing a Donation with its computed distance in km. Useful for front-end feeds.
     */
    public static class DonationDistance {
        public final Donation donation;
        public final double distanceKm;
        public DonationDistance(Donation donation, double distanceKm) {
            this.donation = donation;
            this.distanceKm = distanceKm;
        }
    }

    /**
     * Find nearby donations and return them with computed distances sorted by distance.
     */
    public List<DonationDistance> findNearbyWithDistance(Double lat, Double lng) {
        List<Donation> list = donationRepository.findNearbyAvailableDonations(lat, lng, defaultRadiusKm);
        return list.stream()
                .map(d -> new DonationDistance(d, haversineKm(lat, lng, d.getLatitude(), d.getLongitude())))
                .sorted((a, b) -> Double.compare(a.distanceKm, b.distanceKm))
                .toList();
    }
}
