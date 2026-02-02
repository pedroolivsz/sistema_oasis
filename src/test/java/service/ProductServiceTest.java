package service;

import com.io.github.pedroolivsz.dominio.Product;
import com.io.github.pedroolivsz.repository.ProductRepository;
import com.io.github.pedroolivsz.service.ProductService;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class ProductServiceTest {
        @Test
        public void testCreateProduct() {
                //Cenário
                ProductRepository productRepository = new ProductRepository();
                ProductService productService = new ProductService(productRepository);

                //Ação
                Product created = productService.create("Hidratante", 89, new BigDecimal("99.9"));

                //Verificação
                Assert.assertTrue(created.getName().equals("hidratante"));
                Assert.assertTrue(created.getQuantity() == 89);
                Assert.assertTrue(created.getUnitValue().compareTo(new BigDecimal("99.9")) == 0);
        }
}
