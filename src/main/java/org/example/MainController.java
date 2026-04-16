package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller	// This means that this class is a Controller
@CrossOrigin(origins = "*")
@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class MainController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private PurchaseRepository purchaseRepository;

	@Autowired
	private AiService aiService;

	@PostMapping(path="/addUser") // Map ONLY POST Requests
	public @ResponseBody String addNewUser (@RequestParam String name
			, @RequestParam String email) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		User n = new User();
		n.setName(name);
		n.setEmail(email);
		userRepository.save(n);
		return "Saved User";
	}
	@PostMapping(path="/addWarehouse") // Map ONLY POST Requests
	public @ResponseBody String addNewWH (@RequestBody Warehouse warehouse) {
		warehouseRepository.save(warehouse);
		return "Saved WH";
	}
	@GetMapping(path="/getWarehouse") // Map ONLY GET Requests
	public @ResponseBody Optional<Warehouse> getWH (@RequestParam Long id) {
		return warehouseRepository.findById(id);
	}

	@GetMapping(path="/getProduct") // Map ONLY GET Requests
	public @ResponseBody Optional<Product> getProduct ( @RequestParam Long pid) {
		return productRepository.findById(pid);
	}

	@GetMapping(path="/all")
	public @ResponseBody Iterable<User> getAllUsers() {
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}

	@PostMapping(path="/addPurchase")
	public @ResponseBody String addPurchase (@RequestBody Purchase purchase) {
		purchaseRepository.save(purchase);
		return "Saved Purchase";
	}
	@PostMapping(path="/addProduct")
	public @ResponseBody String addProduct (@RequestBody Product product) {
		productRepository.save(product);
		return "Saved Product";
	}

	@PostMapping(path="/analyse")
	public @ResponseBody String analyse (@RequestParam String prompt) {
		return aiService.processPrompt(prompt);
	}

}
