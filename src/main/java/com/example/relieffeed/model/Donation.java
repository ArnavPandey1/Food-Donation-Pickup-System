package com.example.relieffeed.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String donorName;
    private String volunteerName;
    private String foodType;
    private Double quantityKg;
    private LocalDateTime expiryTimestamp;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private DonationStatus status;

    private String otp;
    private String imageUrl;

    @Version
    private Long version;
}
