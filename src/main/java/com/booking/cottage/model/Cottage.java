package com.booking.cottage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cottage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private double pricePerNight;
    private short bedrooms;
    private short baths;
    @ElementCollection
    @CollectionTable(
            name = "cottage_images",
            joinColumns = @JoinColumn(name = "cottage_id")
    )
    @Column(name = "image_url")
    private List<String> images;
    private float stars;
    private int reviews;
    private short guests;

    public Cottage(String name, String location, double pricePerNight, short bedrooms, short baths) {
        this.name = name;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.bedrooms = bedrooms;
        this.baths = baths;
    }

    public Cottage(String name, String location, double pricePerNight, short bedrooms, short baths, List<String> images, float stars, int reviews, short guests) {
        this.name = name;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.bedrooms = bedrooms;
        this.baths = baths;
        this.images = images;
        this.stars = stars;
        this.reviews = reviews;
        this.guests = guests;
    }
}
