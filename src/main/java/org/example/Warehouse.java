package org.example;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "warehouses")

public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String name;
    private String address;
    private String postalCode;
    private String city;
    private String Country;
    private LocalDateTime createdAt;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Product> productData;
}
