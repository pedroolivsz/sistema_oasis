package com.io.github.pedroolivsz.service;

import java.math.BigDecimal;
import java.util.List;

import com.io.github.pedroolivsz.dominio.Product;
import com.io.github.pedroolivsz.repository.ProdutoRepository;
import com.io.github.pedroolivsz.validation.ProductException;
import com.io.github.pedroolivsz.validation.ProductValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductService {

    public static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProdutoRepository produtoRepository;

    public ProductService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    private Product ensureExists(int id) {
        logger.debug("Verificando existência do produto com ID: {}", id);

        return produtoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado com ID: {}", id);
                    return new ProductException("Produto com ID: " + id + " não existe");
                });
    }

    public Product create(String nome, int quantity, BigDecimal unitValue) {
        logger.info("Criando novo produto: {}", nome);

        Product product = new Product(nome, quantity, unitValue);
        ProductValidator.validateProduct(product);

        Product created = produtoRepository.create(product);
        logger.info("Produto criado com sucesso. ID: {}", created.getId());

        return created;
    }

    public Product update(int id, String nome, int quantidade, BigDecimal valorUnitario) {
        logger.info("Atualizando produto ID: {}", id);

        ensureExists(id);
        Product product = new Product(id, nome, quantidade, valorUnitario);

        ProductValidator.validateProduct(product);

        Product updated = produtoRepository.update(product);
        logger.info("Produto atualizado com sucesso. ID: {}", updated.getId());

        return updated;
    }

    public void delete(int id) {
        logger.info("Deletando produto ID: {}", id);

        ensureExists(id);
        produtoRepository.delete(id);

        logger.info("Produto deletado com sucesso. ID: {}", id);
    }

    public List<Product> listAll() {
        logger.debug("Listando todos os produtos");
        List<Product> products = produtoRepository.listAll();
        logger.debug("Total de produtos encontrados: {}", products.size());

        return List.copyOf(products);
    }

    public Product findById(int id) {
        logger.debug("Buscando produto por ID: {}", id);
        return ensureExists(id);
    }


}
