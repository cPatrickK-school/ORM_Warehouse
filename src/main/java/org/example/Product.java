package org.example;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity // This tells Hibernate to make a table out of this class
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private Integer quantity;
    private String unit;
}
