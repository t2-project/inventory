package de.unistuttgart.t2.payment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import de.unistuttgart.t2.inventory.InventoryService;
import de.unistuttgart.t2.inventory.domain.Product;
import de.unistuttgart.t2.inventory.repository.ProductRepository;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestContext.class)
public class RepositoryTests {
	
	@Test
	public void testAddProduct(@Autowired InventoryService inventoryService, @Autowired ProductRepository productRepository) {
		
		Map<String, Integer> reserved = new HashMap<>();
		reserved.put("foo", 64);
		reserved.put("bar", 64);
		
		
		Product p = new Product("25", "black tea", "black as your soul", 200, 0.50, reserved);
		String id = inventoryService.addProduct(p).getId();
		
		List<Product> products = productRepository.findAll(); 
		
		assertEquals(1, products.size());
		assertEquals(p, products.get(0));
	}
	
	//TODO more tests
	
	@DisplayName("foo")
	@Test
	public void test(@Autowired MongoTemplate mongoTemplate) {
		// given
		DBObject objectToSave = BasicDBObjectBuilder.start().add("key", "value").get();

		// when
		mongoTemplate.save(objectToSave, "collection");

		// then
		assertFalse((mongoTemplate.findAll(DBObject.class, "collection").isEmpty()));
	}
}
