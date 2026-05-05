package com.example.relieffeed.repository;

import com.example.relieffeed.model.Donation;
import com.example.relieffeed.model.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT d FROM Donation d WHERE d.status = 'AVAILABLE' AND " +
           "(6371 * acos(cos(radians(:volunteerLat)) * cos(radians(d.latitude)) * " +
           "cos(radians(d.longitude) - radians(:volunteerLng)) + " +
           "sin(radians(:volunteerLat)) * sin(radians(d.latitude)))) <= :radiusKm")
    List<Donation> findNearbyAvailableDonations(@Param("volunteerLat") Double volunteerLat, 
                                                @Param("volunteerLng") Double volunteerLng, 
                                                @Param("radiusKm") Double radiusKm);

    List<Donation> findByStatus(DonationStatus status);
    
    List<Donation> findByDonorName(String donorName);
    
    List<Donation> findByVolunteerName(String volunteerName);

    @Query("SELECT d.donorName, SUM(d.quantityKg) FROM Donation d WHERE d.status = 'DISTRIBUTED' GROUP BY d.donorName ORDER BY SUM(d.quantityKg) DESC")
    List<Object[]> findTopDonors();
}
