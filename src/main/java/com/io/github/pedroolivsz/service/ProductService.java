package com.io.github.pedroolivsz.service;

import java.math.BigDecimal;
import java.util.List;

import com.io.github.pedroolivsz.dominio.Product;
import com.io.github.pedroolivsz.repository.ProdutoRepository;
import com.io.github.pedroolivsz.repository.RepositoryException;
import com.io.github.pedroolivsz.validation.ProductException;
import com.io.github.pedroolivsz.validation.ProductValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsável pela lógica de negocio de produto.
 *
 * <p>Esta classe atua como intermediaria entre controller e repository,
 * implementando regras de negócio, validações e tratamento de exceções</p>
 *
 * <p>Características principais</p>
 * <ul>
 *     <li>Validação completa de regra de negócio</li>
 *     <li>Logging detalhado de operações</li>
 *     <li>Tratamento centralizado de exceções</li>
 *     <li>Encapsulamento da lógica de domínio</li>
 * </ul>
 *
 * @author João Pedro
 */

public class ProductService {
    public static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private static final String ERROR_PRODUCT_NOT_FOUND = "Produto com ID %d não encontrado";
    private static final String ERROR_DUPLICATE_NAME = "Já existe um produto com o nome '%s'";
    private static final String ERROR_INSUFFICIENT_STOCK = "Estoque insuficiente. Disponivel: %d, solicitado: %d";
    private static final String ERROR_INVALID_QUANTITY = "Quantidade deve ser maior que zero";
    private static final String ERROR_INVALID_PRICE = "Preço deve ser maior que zero";

    private final ProdutoRepository produtoRepository;

    public ProductService(ProdutoRepository produtoRepository) {
        if(produtoRepository == null) throw new IllegalArgumentException("ProdutoRepository não pode ser null");
        this.produtoRepository = produtoRepository;
    }



    public Product create(String name, int quantity, BigDecimal unitValue) {
        logger.info("Iniciando criação de produto: nome='{}', quantidade={}, valor={}", name, quantity, unitValue);

        try {
            Product product = new Product(name, quantity, unitValue);
            ProductValidator.validateProduct(product);

            validateBusinessRules(product);

            Product created = produtoRepository.create(product);

            logger.info("Produto criado com sucesso. ID: {}, nome: '{}'", created.getId(), created.getName());

            return created;
        } catch (ProductException | IllegalArgumentException e) {
            logger.error("Erro de validação ao criar o produto: {}", e.getMessage());
            throw e;
        } catch (RepositoryException e) {
            logger.error("Erro ao persistir produto no banco de dados", e);
            throw new ServiceException("Erro ao criar produto. Tente novamente.", e);
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar produto.", e);
            throw new ServiceException("Erro inesperado ao criar produto.", e);
        }
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

    private Product ensureExists(int id) {
        logger.debug("Verificando existência do produto com ID: {}", id);

        return produtoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado com ID: {}", id);
                    return new ProductException("Produto com ID: " + id + " não existe");
                });
    }

    private void validateBusinessRules(Product product) {
        if(product.getQuantity() < 0) throw new ProductException("Quantidade não pode ser negotiva");
        if(product.getUnitValue().compareTo(BigDecimal.ZERO) < 0) throw new ProductException("Valor não pode ser negativo");
    }
}
