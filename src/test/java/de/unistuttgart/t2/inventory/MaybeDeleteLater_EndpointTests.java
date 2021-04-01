package de.unistuttgart.t2.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.unistuttgart.t2.common.domain.Product;
import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.ProductRepository;


@ContextConfiguration(classes = TestContext.class)
@DataMongoTest
// TODO : How do i make this stupid thing NOT use the application context BUT the TESTCONTEXT?!?!?!
class MaybeDeleteLater_EndpointTests {
	
	//@LocalServerPort
	private int port = 12345; 

	@Autowired
	private RestTemplate restTemplate;

	private int initialSize;
	
	@BeforeEach
	public void populateRepository(@Autowired ProductRepository repository) {
		InventoryItem noReservation = new InventoryItem("nores", "panda", "desc1", 3, 32.4, Map.of());
		InventoryItem withReservation = new InventoryItem("withres", "cat", "desc2", 150, 0.6, Map.of("sessio1", 23));
		repository.save(noReservation);
		repository.save(withReservation);
		
		initialSize = repository.findAll().size();
	}
	
	
	@Test
	public void getNoReservationInventoryTest(@Autowired ProductRepository repository) throws JsonMappingException, JsonProcessingException {
		//make request
		String response = restTemplate.getForObject("http://localhost:" + port + "/inventory/nores", String.class);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response);

		//assert deserialization
		Product product =  mapper.treeToValue(root.path("content"), Product.class);
		assertNotNull(product);
		assertNotNull(product.getId());
		assertNotNull(product.getName());
		
		InventoryItem expected = repository.findById("nores").get();
		
		assertEquals(expected.getId(), product.getId());
		assertEquals(expected.getName(), product.getName());
		assertEquals(expected.getUnits(), product.getUnits());
		assertEquals(expected.getPrice(), product.getPrice());
	}
	
	@Test
	public void getWithReservationInventoryTest(@Autowired ProductRepository repository) throws JsonMappingException, JsonProcessingException {
		//make request
		String response = restTemplate.getForObject("http://localhost:" + port + "/inventory/withres", String.class);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response);

		//assert deserialization
		Product product =  mapper.treeToValue(root.path("content"), Product.class);
		assertNotNull(product);
		assertNotNull(product.getId());
		assertNotNull(product.getName());
		
		InventoryItem expected = repository.findById("withres").get();
		
		assertEquals(expected.getId(), product.getId());
		assertEquals(expected.getName(), product.getName());
		assertEquals(expected.getUnits(), product.getUnits());
		assertEquals(expected.getPrice(), product.getPrice());
	}
	
	@Test
	public void postCartTest(@Autowired ProductRepository repository) {
		//make request
		Product expected = new Product("newid", "newname", "newdescriprion", 25, 3.0);
		restTemplate.postForObject("http://localhost:" + port + "/inventory", expected, Void.class);
		
		//assert
		assertEquals(initialSize, repository.count());
		assertTrue(repository.existsById(expected.getId()));
		
		InventoryItem actual = repository.findById(expected.getId()).get();
		
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getUnits(), actual.getUnits());
		assertEquals(expected.getPrice(), actual.getPrice());
		
		assertNotNull(actual.getReservations());
		assertTrue(actual.getReservations().isEmpty());
	}
	
	@Test
	public void putCartTest(@Autowired ProductRepository repository) {
		// do i support put?
	}
	
	@Test
	public void deleteCartTest(@Autowired ProductRepository repository) {
		// do i support delete?
	}
}

