package org.example;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Product> products;
    @OneToOne(cascade = CascadeType.ALL)
    private Warehouse warehouse;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
    private LocalDateTime createdAt;
}
