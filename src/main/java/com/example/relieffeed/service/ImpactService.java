package com.example.relieffeed.service;

import com.example.relieffeed.repository.DonationRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
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
        Number totalMealsSaved = donationRepository.getTotalMealsSaved();
        return totalMealsSaved != null ? totalMealsSaved.intValue() : 0;
    }

    public long getActiveDonationCount() {
        return donationRepository.countActiveDonations();
    }

    public long getDistributedDonationCount() {
        return donationRepository.countDistributedDonations();
    }

    public List<Map<String, Object>> getTopDonors() {
        List<Object[]> results = donationRepository.findTopDonors();
        return results.stream().map(result -> {
            String donorName = result[0] != null ? result[0].toString() : "Unknown";
            double totalKg = asDouble(result[1]);
            int mealsSaved = asInt(result[2]);
            int donationCount = asInt(result[3]);

            Map<String, Object> donorSummary = new LinkedHashMap<>();
            donorSummary.put("donorName", donorName);
            donorSummary.put("totalKg", totalKg);
            donorSummary.put("mealsSaved", mealsSaved);
            donorSummary.put("donationCount", donationCount);
            return donorSummary;
        }).toList();
    }

    private double asDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value == null) {
            return 0.0;
        }
        return Double.parseDouble(value.toString());
    }

    private int asInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value.toString());
    }
}
