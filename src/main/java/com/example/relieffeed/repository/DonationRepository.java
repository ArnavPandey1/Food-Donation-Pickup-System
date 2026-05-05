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

        @Query(value = """
            SELECT COALESCE(SUM(CASE WHEN status = 'DISTRIBUTED' THEN quantity_kg * 4 ELSE 0 END), 0)
            FROM donation
            """, nativeQuery = true)
        Number getTotalMealsSaved();

        @Query(value = """
            SELECT donor_name,
               COALESCE(SUM(quantity_kg), 0) AS total_kg,
               COALESCE(SUM(quantity_kg * 4), 0) AS meals_saved,
               COUNT(*) AS donation_count
            FROM donation
            WHERE status = 'DISTRIBUTED' AND donor_name IS NOT NULL
            GROUP BY donor_name
            ORDER BY meals_saved DESC, total_kg DESC, donor_name ASC
            """, nativeQuery = true)
    List<Object[]> findTopDonors();

        @Query(value = """
            SELECT COUNT(*)
            FROM donation
            WHERE status IN ('AVAILABLE', 'CLAIMED', 'PICKED_UP')
            """, nativeQuery = true)
        long countActiveDonations();

        @Query(value = """
            SELECT COUNT(*)
            FROM donation
            WHERE status = 'DISTRIBUTED'
            """, nativeQuery = true)
        long countDistributedDonations();
}
