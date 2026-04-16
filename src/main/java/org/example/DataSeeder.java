package org.example;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDatabase(PurchaseRepository purchaseRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository, UserRepository userRepository) {
        return args -> {
            // Nur generieren, wenn die Tabelle leer ist
            if (productRepository.count() < 1000) {
                List<Product> products = new ArrayList<>();
                String[] categories = {"Electronics", "Tools", "Food", "Clothing", "Furniture", "Office"};
                String[] units = {"pcs", "kg", "boxes", "liters", "packs"};
                String[] prefixes = {"Pro", "Super", "Basic", "Advanced", "Eco", "Premium"};
                String[] items = {"Widget", "Gadget", "Device", "Tool", "Module", "System"};

                Random random = new Random();

                for(int i = 1; i <= 50; i++) {
                    Purchase p = new Purchase();
                    products.clear(); // Liste für jedes Purchase leeren
                    for(int j = 0; j < 5; j++) {
                        Product pr = new Product();
                        String randomPrefix = prefixes[random.nextInt(prefixes.length)];
                        String randomItem = items[random.nextInt(items.length)];
                        pr.setName(randomPrefix + " " + randomItem + " Model-" + i);
                        pr.setCategory(categories[random.nextInt(categories.length)]);
                        pr.setQuantity(random.nextInt(500) + 1); // Mengen zwischen 1 und 500
                        pr.setUnit(units[random.nextInt(units.length)]);
                        products.add(pr);
                    }

                    Warehouse w = new Warehouse();
                    w.setName("Warehouse " + i);
                    w.setAddress("Address " + i);
                    w.setPostalCode("PostalCode " + i);
                    w.setCity("City " + i);
                    w.setCountry("Country " + i);
                    p.setWarehouse(w);

                    User u = new User();
                    u.setName("User " + i);
                    u.setEmail("user" + i + "@example.com");
                    p.setUser(u);

                    p.setProducts(products);
                    purchaseRepository.save(p);
                }
                System.out.println("Items generated");
            }
        };
    }
}