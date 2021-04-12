package de.unistuttgart.t2.inventory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.unistuttgart.t2.common.Product;
import de.unistuttgart.t2.common.ReservationRequest;
import de.unistuttgart.t2.inventory.repository.InventoryItem;

@RestController
public class InventoryController {
	
	@Autowired
	InventoryService inventoryService;

	// put, if i view it as 'updating the product'
	// post, if i view it as 'creating new reservation....
	@PostMapping("/inventory/reservation")
	public Product addReservation(@RequestBody ReservationRequest body){
		InventoryItem item = inventoryService.makeReservation(body.getProductId(), body.getSessionId(), body.getUnits());
		return new Product(item.getId(),item.getName(),item.getAvailableUnits(),item.getPrice());
		
		// TODO Error handling?? 
		// current response : {"timestamp":"2021-03-29T08:46:50.240+00:00","status":500,"error":"Internal Server Error","message":"","path":"/reservation"}
	}
}
