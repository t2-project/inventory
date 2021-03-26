package de.unistuttgart.t2.inventory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.unistuttgart.t2.common.domain.Product;

@RestController
public class InventoryController {
	
	@Autowired
	InventoryService inventoryService;
	
	@GetMapping("/get/{pid}")
	@ResponseBody
	public Product get(@PathVariable("pid") final String pid) {
		return inventoryService.getProduct(pid);
	}

	@GetMapping("/get")
	@ResponseBody
	public List<Product> getAll() {
		return inventoryService.getAllProducts();
	}

}
