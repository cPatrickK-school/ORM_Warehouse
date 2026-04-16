## Farrenkopf
## 09.04.2026

### 0. SETUP DB

Bei der DB habe mich für **PostgreSQL** entschieden, da ich mit dieser erfahrener bin. Als Externes Tool zum Bearbeiten der DB verwende ich **pgAdmin**. Wie folgt schaut meine DB-connection aus:
`jdbc:postgresql://localhost:5432/orm_milch`

### 1. APP. PROPS & HIBERNATE GENERATION

Damit sich Java mit der DB verbinden kann, muss es die Metadaten zur Verbindung kennen. In diesem Fall werden diese in die ``application.properties`` geschrieben. Für dieses Bsp. steht folgendes im File:

``` properties
spring.application.name=demo  
spring.jpa.hibernate.ddl-auto=update  
spring.datasource.url=jdbc:postgresql://localhost:5432/orm_milch  
spring.datasource.username=farrenkopf  
spring.datasource.password= password  
spring.datasource.driver-class-name=org.postgresql.Driver
```

Dank dem 2. Eintrag (ddl-auto = update) erstellt hibernate basierend auf gegebenes @Entity-tagged Files und somit müssen wir selber kein schema und keine entrydata erzeugen.

### 2. Ausarbeitung GK

Ich habe für ein jedes "Entityfile" folgende Annotationen verwendet:
```` java
@Entity // Zeigt das diese Klasse DB-Data beinhaltet 
@Data  // Autogen Boilerplate (Lombok)
@Table(name = "nameDerTabelle") // name der Zieltabelle
````

Somit schaut dann eine jede Datenklasse strukturell ca. so aus:
````java
@Entity  
@Data  
@Table(name = "warehouses")  
  
public class Warehouse {  
    @Id  
    @GeneratedValue(strategy = GenerationType.AUTO)  
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
````
Die Repositoryklassen verbinden die DB mit Java, indem davon generierte Objekte CRUD-Methoden bereitstellen. Dafür müssen die Repositories so aufgebaut sein:
````java
@Repository  
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {  
// Jpa-Repository mit zu speicherndem Objekt und Datentyp der ID
}
````
### 3. Befüllen der DB (Endpunkte & Inserts)

um wie verlangt die DB zu befüllen, habe ich Gemini Inserts und Jsons für die Endpunkte generieren lassen. Da ich die Generationmethode auf Auto gesetzt habe, haben die direkten DB-inserts nicht mehr funktioniert. Stattdessen habe ich JSONs an den Endpunkt geschickt, um die DB zu befüllen. So Schaut eine Objektrepräsentation aus:

````json
{

    "name": "Toronto Tech Depot",

    "address": "100 Front St W",

    "postalCode": "M5J 1E3",

    "city": "Toronto",

    "country": "Canada",

    "createdAt": "2026-06-18T14:00:00",

    "productData": [

      {

        "name": "Mechanical Keyboards",

        "category": "Electronics",

        "quantity": 400,

        "unit": "pieces"

      },

      {

        "name": "Gaming Mice",

        "category": "Electronics",

        "quantity": 600,

        "unit": "pieces"

      },

      {

        "name": "USB-C Hubs",

        "category": "Accessories",

        "quantity": 1000,

        "unit": "pieces"

      },

      {

        "name": "Monitor Stands",

        "category": "Office Supplies",

        "quantity": 200,

        "unit": "pieces"

      }

    ]

  }
````

Dafür habe ich folgenden Endpunkt geschrieben:
````java
@PostMapping(path="/addWarehouse") // Map ONLY POST Requests  
public @ResponseBody String addNewWH (@RequestBody Warehouse warehouse) {  
    warehouseRepository.save(warehouse);  
    return "Saved WH";  
}
````
### 4. Fragen
- ***What is ORM and how is JPA used*?**

Object-Relational Mapping automatically connects your Java objects directly to the tables in a database. The Java Persistence API provides the official rules for mapping these objects to the database. Developers usually use Hibernate as the main tool to perform this required JPA mapping.

- ***What is the application.properties used for and where must it be stored?***

The `application.properties` file stores essential configuration settings, such as your specific database connection credentials. You must store this crucial file inside the `src/main/resources` directory of your Spring project.

- ***Which annotations are frequently used for entity types? Which key points must be observed?***

Use the `@Entity` annotation to tell JPA that a class represents a specific database table. You must mark the primary key field of your entity class using the `@Id` annotation. Always remember to include a required default no-argument constructor inside your specific entity class.

- ***What methods do you need for CRUD operations?***

Spring Data JPA automatically provides built-in methods like `save()` for creating and updating database entities. You can easily fetch specific database records by using the provided `findById()` or `findAll()` methods. To remove existing records from your database, simply use the `deleteById()` or the `delete()` methods.

### 5. Purchases
für die EK habe ich die Purchases Tabelle inkludiert, welche auf Basis folgender Klasse generiert wurden:
```` java
@Data  
@Entity  
@Table(name = "purchases")  
public class Purchase {  
    @Id  
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)  
    private Long id;  
    @OneToMany(cascade = CascadeType.ALL)  
    private List<Product> products;  
    @OneToOne(cascade = CascadeType.ALL)  
    private Warehouse warehouse;  
    @OneToOne(cascade = CascadeType.ALL)  
    private User user;  
    private LocalDateTime createdAt;  
}
````

Ich habe dann Mittels einem Endpunkt Testdaten hinzugefügt.
```` Java
@PostMapping(path="/addPurchase")  
public @ResponseBody String addPurchase (@RequestBody Purchase purchase) {  
    purchaseRepository.save(purchase);  
    return "Saved Purchase";  
}
````

```` java
{

  

  "products": [

    {

      "name": "Wireless Cat",

      "category": "Electronics",

      "quantity": 2,

      "unit": "pcs"

    },

    {

      "name": "Mechanical Ziegel",

      "category": "Electronics",

      "quantity": 1,

      "unit": "pcs"

    }

  ],

  "warehouse": {

    "name": "Central Logistics Hub",

    "address": "123 Supply Chain Way",

    "postalCode": "10115",

    "city": "Berlin",

    "country": "Germany",

    "createdAt": "2024-01-15T08:30:00"

  },

  "user": {

    "name": "Chi Nese",

    "email": "chi.doe@example.com"

  },

  "createdAt": "2026-04-10T10:36:41"

}
````

### 6. AI-gestützte Inhaltsanalyse

Um dem Nutzer ein Hilfsmittel zur Datenanalyse zu geben habe ich eine lokale Ollama-Instanz verwendet. Mittels einem generiertem Service und  Endpunkt.
````Java
package org.example;  
  
  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.*;  
import org.springframework.stereotype.Service;  
import org.springframework.web.client.RestTemplate;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
  
@Service  
public class AiService {  
    @Autowired  
    WarehouseRepository warehouseRepository;  
  
    @Autowired  
    private PurchaseRepository purchaseRepository;  
  
    private static String MODEL = "llama3";  
    private static String URL = "http://localhost:11434/api/generate";  
    private static String SYSTEM_PROMPT = """  
            Du bist Datenanalyst bei einem Online-Shop, der Produkte verkauft. Du hast Zugriff auf die folgenden Daten:                - Eine Liste von Produkten, die in den Lagern verfügbar sind, mit Informationen über die Produkt-ID, den Namen, die Kategorie, den Preis und die verfügbare Menge.                - Eine Liste von Käufen der Kunden, mit Informationen über die Kauf-ID, die Produkt-ID, die Menge, den Gesamtpreis und das Kaufdatum.            Hilf deinem Vorgesetzten, indem du ihm seine Frage zu den Daten beantwortest.                        Die Daten lauten wie folgt:  
            """;  
  
    public String processPrompt(String userPrompt) {  
        List< Warehouse> warehouses = warehouseRepository.findAll();  
        List<Purchase> purchases = purchaseRepository.findAll();  
        //purchases.forEach(p -> p.setWarehouse(null));  
        SYSTEM_PROMPT += "Warehouses: " + warehouses.toString() + "\n";  
        SYSTEM_PROMPT += "Purchases: " + purchases.toString() + "\n";  
        return sendRequest( buildRequestBody(userPrompt));  
    }  
  
    private Map<String, Object> buildRequestBody(String userPrompt) {  
        Map<String, Object> requestBody = new HashMap<>();  
        requestBody.put("model", MODEL);  
        requestBody.put("system", SYSTEM_PROMPT);  
        requestBody.put("prompt", userPrompt);  
        requestBody.put("max_tokens", 50);  
        requestBody.put("stream", false);  
        return requestBody;  
    }  
  
    private String sendRequest(Map<String, Object> requestBody) {  
        HttpHeaders headers = new HttpHeaders();  
  
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);  
  
        ResponseEntity<Map> response = new RestTemplate().exchange(  
                URL,  
                HttpMethod.POST,  
                request,  
                Map.class  
        );  
  
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {  
            return response.getBody().get("response").toString();  
        }  
        return "Keine Antwort vom Modell";  
  
    }  
}
````
Der Output mittels Postman schaut wie folgt aus:
![[Pasted image 20260414174028.png]]

### 7. Entries

KI-gestützt habe ich einen DataSeeder geschrieben, mit welchem die verlangten entries gemacht wurden. Solange weniger als 1000 produkte existieren werden 250 Produkte und 50 purchases sowie auch verbundene entities generated.
````java
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

````

