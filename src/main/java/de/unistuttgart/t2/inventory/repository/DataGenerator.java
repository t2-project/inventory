package de.unistuttgart.t2.inventory.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import de.unistuttgart.t2.common.CartContent;

@Component
public class DataGenerator {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	ProductRepository repository;
	
	@Value("${inventory.size}")
	protected String inventorySIzeAsString;
	
	protected int inventorySIze = 10;
	
	RestTemplate template = new RestTemplate();
	
	@Value("${t2.cart.url}")
	private String cartUrl; 
	
	private int cartSize = 3;
	
	Random random = new Random(5);

	@PostConstruct
	protected void generateProducts() {
		//parse size value to int (because i can only load properites as strings... i guess??)
		try {
			inventorySIze = Integer.parseInt(inventorySIzeAsString);
		} catch (NumberFormatException e) {
			LOG.info(String.format("could not parse size of \"%s\" to int. contiune with size of %d", inventorySIzeAsString, inventorySIze));
		}
		
		if (repository.count() >= inventorySIze) {
			LOG.info(String.format("repository already contains %d entries. not adding new entries.", repository.count()));
			return;
		}
		
		if (inventorySIze > PRODUCTNAMES.length) {
			inventorySIze = PRODUCTNAMES.length;
		}

		LOG.info(String.format("repository too small. generate %d new entries.", inventorySIze));
		
		for (int i = (int) repository.count(); i < inventorySIze; i++) {
			InventoryItem product = new InventoryItem();

			product.setName(PRODUCTNAMES[i]);
			product.setUnits(random.nextInt(50) + 20);
			product.setPrice(random.nextInt(10) + random.nextDouble());
			product.setDescription("very nice " + PRODUCTNAMES[i] + " tea");
		
			repository.save(product);
		}
		
		generateReservations();
	}

	/**
	 * if cart service is available, generate reservations
	 * 
	 */
	@Transactional
	private void generateReservations() {
		//assert cart availability
		try {
			template.getForObject(cartUrl, String.class);
		} catch (Exception e) {
			LOG.info("not generating reservations, because: " + e.getMessage());
			return;
		}
		
		List<InventoryItem> items = repository.findAll(); 
		if (items.size() == 0) {
			return;
		}
		
		for (int i = 0; i < cartSize; i++) {
			String sessionId = "sessionid" + i;
			int numberProductInCart = random.nextInt(10);
			CartContent cartContent= new CartContent();
			
			for (int j = 0; j < numberProductInCart; j++) {
				InventoryItem randomItem = items.get(random.nextInt(items.size()));
				
				int reservedUnits = random.nextInt(randomItem.getAvailableUnits());
				
				if (reservedUnits > 0) {
					cartContent.getContent().put(randomItem.getId(), reservedUnits);
					randomItem.addReservation(sessionId, reservedUnits);
				}
			}
			template.put(cartUrl + sessionId, cartContent);
			repository.saveAll(items);
		}
	}
	
	private static final String[] PRODUCTNAMES = { "Earl Grey (loose)", "Assam (loose)", "Darjeeling (loose)",
			"Frisian Black Tee (loose)", "Anatolian Assam (loose)", "Earl Grey (20 bags)", "Assam (20 bags)",
			"Darjeeling (20 bags)", "Ceylon (loose)", "Ceylon (20 bags)", "House blend (20 bags)",
			"Assam with Ginger (20 bags)", "Sencha (loose)", "Sencha (15 bags)", "Sencha (25 bags)",
			"Earl Grey Green (loose)", "Earl Grey Green (15 bags)", "Earl Grey Green (25 bags)", "Matcha 30 g",
			"Matcha 50 g", "Matcha 100 g", "Gunpowder Tea (loose)", "Gunpowder Tea (15 bags)",
			"Gunpowder Tea (25 bags)", "Camomile (loose)", "Camomile (15 bags)", "Peepermint (loose)",
			"Peppermint (15 bags)", "Peppermint (15 bags)", "Sweet Mint (loose)", "Sweet Mint (15 bags)",
			"Sweet Mint (25 bags)", "Lemongrass (loose)", "Lemongrass (20 bags)", "Chai Mate (15 bags)",
			"Chai Mate (25 bags)", "Stomach Soothing Tea (15 bags)", "Headache Soothing Tea (15 bags)",
			"Rooibos Pure (loose)", "Rooibos Pure (20 bags)", "Rooibos Orange (loose)", "Rooibos Orange (20 bags)",
			"Rooibos Coconut (loose)", "Rooibos Coconut (20 bags)", "Rooibos Vanilla (loose)", "Rooibos Pure (20 bags)",
			"Rooibos Ginger (loose)", "Rooibos Pure (20 bags)", "Rooibos Grapefruit (loose)", "Rooibos Pure (20 bags)",
			"White Tea (loose)", "White Tea (15 bags)", "White Tea (25 bags)", "White Chai (loose)",
			"White Chai (15 bags)", "White Chai (25 bags)", "Pai Mu Tan White (loose)", "Pai Mu Tan White (15 bags)",
			"Pai Mu Tan White (25 bags)", "White Apricot (loose)", "White Apricot (15 bags)",
			"White Apricot (25 bags)" };

}
