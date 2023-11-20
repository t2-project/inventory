package de.unistuttgart.t2.inventory.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class DataGeneratorTest {

    @Autowired
    ProductRepository productRepository;

    DataGenerator generator;

    @BeforeEach
    public void setup() {
        generator = new DataGenerator(productRepository, 10);
        productRepository.deleteAll();
    }

    @AfterEach
    public void cleanRepo() {
        productRepository.deleteAll();
    }

    @Test
    public void testDefaultGeneration() {
        assertEquals(0, productRepository.count());

        generator.generateProducts();

        assertEquals(10, productRepository.count());
    }

    @Test
    public void testAdditionalGeneration() {
        assertEquals(0, productRepository.count());
        generator.generateProducts();
        assertEquals(10, productRepository.count());

        generator = new DataGenerator(productRepository, 15);
        generator.generateProducts();

        assertEquals(15, productRepository.count());
    }

    // do not generate new items if there are already enough in the repo
    @Test
    public void testNoAdditionalGeneration() {
        assertEquals(0, productRepository.count());
        generator.generateProducts();
        assertEquals(10, productRepository.count());

        generator = new DataGenerator(productRepository, 5);
        generator.generateProducts();

        assertEquals(10, productRepository.count());
    }

    @Test
    public void testRestock() {
        assertEquals(0, productRepository.count());
        generator.generateProducts();
        generator.restockProducts();

        List<InventoryItem> items = productRepository.findAll();

        assertFalse(items.isEmpty());

        for (InventoryItem item : items) {
            assertEquals(Integer.MAX_VALUE, item.getUnits());
        }
    }
}
