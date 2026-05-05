package com.example.relieffeed.service;

import com.example.relieffeed.model.Donation;
import com.example.relieffeed.model.DonationStatus;
import com.example.relieffeed.repository.DonationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ImpactService {

    private final DonationRepository donationRepository;

    public ImpactService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public int calculateMealsSaved(Double quantityKg) {
        if (quantityKg == null) return 0;
        return (int) (quantityKg * 4);
    }

    public int getTotalMealsSaved() {
        List<Donation> distributed = donationRepository.findByStatus(DonationStatus.DISTRIBUTED);
        return distributed.stream()
                .mapToInt(d -> calculateMealsSaved(d.getQuantityKg()))
                .sum();
    }

    public List<Map<String, Object>> getTopDonors() {
        List<Object[]> results = donationRepository.findTopDonors();
        return results.stream().map(result -> {
            String donorName = (String) result[0];
            Double totalKg = (Double) result[1];
            return Map.of(
                "donorName", donorName != null ? donorName : "Unknown",
                "mealsSaved", calculateMealsSaved(totalKg)
            );
        }).toList();
    }
}
