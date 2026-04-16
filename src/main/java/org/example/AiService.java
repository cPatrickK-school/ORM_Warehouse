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
            Du bist Datenanalyst bei einem Online-Shop, der Produkte verkauft. Du hast Zugriff auf die folgenden Daten:
                - Eine Liste von Produkten, die in den Lagern verfügbar sind, mit Informationen über die Produkt-ID, den Namen, die Kategorie, den Preis und die verfügbare Menge.
                - Eine Liste von Käufen der Kunden, mit Informationen über die Kauf-ID, die Produkt-ID, die Menge, den Gesamtpreis und das Kaufdatum.
            Hilf deinem Vorgesetzten, indem du ihm seine Frage zu den Daten beantwortest.
            
            Die Daten lauten wie folgt:
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