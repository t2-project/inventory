package de.unistuttgart.t2.inventory.repository;

import java.util.List;
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

/**
 * Generates new products into the product repository or or restocks existing ones. 
 * 
 * Generation is always triggered after initialisation.
 * 
 * @author maumau
 *
 */
@Component
public class DataGenerator {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    ProductRepository repository;

    RestTemplate template = new RestTemplate();

    @Value("${t2.inventory.size:20}")
    protected int inventorySize;

    @Value("${t2.cart.url}")
    private String cartUrl;
 
    private int cartSize = 3;
    private Random random = new Random(5);

    /**
     * Generates products into the product repository.
     * 
     * Also generates cart content and reservations, if the cart service is available.
     */
    @PostConstruct
    public void generateProducts() {
        if (repository.count() >= inventorySize) {
            LOG.info(String.format("repository already contains %d entries. not adding new entries.",
                    repository.count()));
            return;
        }

        if (inventorySize > PRODUCTNAMES.length) {
            inventorySize = PRODUCTNAMES.length;
        }

        LOG.info(String.format("repository too small. generate %d new entries.", inventorySize));

        for (int i = (int) repository.count(); i < inventorySize; i++) {
            String name = PRODUCTNAMES[i];
            int units = random.nextInt(500) + 42;
            double price = random.nextInt(10) + random.nextDouble();
            String description = "very nice " + PRODUCTNAMES[i] + " tea";

            InventoryItem product = new InventoryItem(null, name, description, units, price);


            repository.save(product);
        }
     }

    /**
     * restock products in the repository.
     * 
     * at some point all products will be sold out. thus there must be an option the
     * restock them.
     * 
     * 
     */
    @Transactional
    public void restockProducts() {
        int maxUnits = Integer.MAX_VALUE; // maybe make this a parameter later ???
        List<InventoryItem> items = repository.findAll();

        for (InventoryItem item : items) {
            item.setUnits(maxUnits);
        }

        repository.saveAll(items);
        LOG.info(String.format("Restocked all products to %d", maxUnits));
    }

    // Predefined products from original tea store
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
