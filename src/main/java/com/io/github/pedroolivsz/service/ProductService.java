package com.io.github.pedroolivsz.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.io.github.pedroolivsz.dominio.Product;
import com.io.github.pedroolivsz.repository.ProductRepository;
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
    //=============== Constantes ===============

    public static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    //Mensagens de erros padronizadas
    private static final String ERROR_PRODUCT_NOT_FOUND = "Produto com ID %d não encontrado";
    private static final String ERROR_DUPLICATE_NAME = "Já existe um produto com o nome '%s'";
    private static final String ERROR_INSUFFICIENT_STOCK = "Estoque insuficiente. Disponivel: %d, solicitado: %d";
    private static final String ERROR_INVALID_QUANTITY = "Quantidade deve ser maior que zero";
    private static final String ERROR_INVALID_PRICE = "Preço deve ser maior que zero";

    //=============== Dependências ===============

    private final ProductRepository productRepository;

    //=============== Construtor ===============

    /**
     * Construtor com injeção de dependẽncias
     *
     * @param productRepository repository de produtos
     * @throws IllegalArgumentException se o repository for null
     */

    public ProductService(ProductRepository productRepository) {
        if(productRepository == null) throw new IllegalArgumentException("ProdutoRepository não pode ser null");
        this.productRepository = productRepository;
    }

    //=============== Métodos CRUD ===============

    /**
     * Cria um novo produto.
     *
     * @param name nome do produto
     * @param quantity quantidade inicial
     * @param unitValue valor unitário
     * @return produto com ID gerado
     * @throws ProductException se houver erro de validação ou regra de negócio
     * @throws ServiceException se houver erro na operação
     */
    public Product create(String name, int quantity, BigDecimal unitValue) {
        logger.info("Iniciando criação de produto: nome='{}', quantidade={}, valor={}", name, quantity, unitValue);

        try {
            //Validação básica
            Product product = new Product(name, quantity, unitValue);
            ProductValidator.validateProduct(product);

            //Regras de negócio adicionais
            validateBusinessRules(product);

            //Persistência
            Product created = productRepository.create(product);

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

    /**
     * Cria um produto dentro de uma transação.
     *
     * @param name nome do produto
     * @param quantity quantidade inicial
     * @param unitValue valor unitário
     * @return produto com o ID gerado
     * @throws ServiceException se houver erro na operação
     */
    public Product createWithTransaction(String name, int quantity, BigDecimal unitValue) {
        logger.info("Iniciando criação de produto (transação): '{}'", name);

        try {
            Product product = new Product(name, quantity, unitValue);
            ProductValidator.validateProduct(product);

            validateBusinessRules(product);

            Product created = productRepository.createWithTransaction(product);

            logger.info("Produto criado com transação. ID: {}, nome: '{}'", created.getId(), created.getName());

            return created;
        } catch (Exception e) {
            logger.error("Erro ao criar produto com transação", e);
            throw new ServiceException("Erro ao criar produto com transação", e);
        }
    }

    /**
     * Atualiza um produto existente.
     *
     * @param id ID do produto
     * @param nome novo nome
     * @param quantidade nova quantidade
     * @param valorUnitario novo valor unitário
     * @return produto atualizado
     * @throws ProductException se o produto não existir ou houver erro na validação
     * @throws ServiceException se houver erro na operação
     */
    public Product update(int id, String nome, int quantidade, BigDecimal valorUnitario) {
        logger.info("Iniciando atualização do produto ID: {}", id);

        try {
            Product existing = ensureExists(id);
            logger.debug("Produto encontrado para atualização: {}", existing.getId());

            Product product = new Product(id, nome, quantidade, valorUnitario);
            ProductValidator.validateProduct(product);
            validateBusinessRules(product);

            Product updated = productRepository.update(product);

            logger.info("Produto atualizado com sucesso. ID: {}, Antigo: '{}', Novo: '{}'",
                    updated.getId(), existing.getName(), updated.getName());

            return updated;
        } catch (ProductException | IllegalArgumentException e) {
            logger.error("Erro de validação ao atualizar produto ID {}: {}", id, e.getMessage());
            throw e;
        } catch (RepositoryException e) {
            logger.error("Erro ao atualizar produto ID {} no banco de dados", id, e);
            throw new ServiceException("Erro ao atualizar produto", e);
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar produto ID {}", id, e);
            throw new ServiceException("Erro inesperado ao atualizar produto", e);
        }
    }

    /**
     * Atualiza parcialmente um produto existente.
     *
     * @param id ID do produto
     * @param updates mapa com os campos a atualizar
     * @return produto atualizado
     * @throws ServiceException se houver erro na operação
     */
    public Product partialUpdate(int id, Map<String, Object> updates) {
        logger.info("Iniciando atualização parcial do produto ID: {} - campos: {}", id, updates.keySet());

        try {
            ensureExists(id);
            validatePartialUpdate(updates);

            Product updated = productRepository.partialUpdate(id, updates);
            logger.info("Produto ID {} atualizado parcialmente com sucesso", id);

            return updated;
        } catch (Exception e) {
            logger.error("Erro ao atualizar parcialmente o produto ID: {}", id, e);
            throw new ServiceException("Erro na atualização parcial", e);
        }
    }

    /**
     * Remove um produto.
     *
     * @param id ID do produto
     * @throws ProductException se o produto não existir
     * @throws ServiceException se houver erro na operação
     */
    public void delete(int id) {
        logger.info("Iniciando exclusão do produto ID: {}", id);

        try {
            Product existing = ensureExists(id);
            //Regras de negócio antes de deletar
            validateBusinessRules(existing);

            productRepository.delete(id);

            logger.info("Produto deletado com sucesso. ID: {}, nome = '{}'", id, existing.getName());
        } catch (ProductException e) {
            logger.warn("Tentativa de deletar produto inexistente. ID: {}", id, e);
            throw e;
        } catch (RepositoryException e) {
            logger.error("Erro ao deletar produto ID {} do banco de dados", id, e);
            throw new ServiceException("Erro ao deletar produto", e);
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar produto ID {}", id, e);
            throw new ServiceException("Erro inesperado ao deletar produto", e);
        }
    }

    //=============== Métodos de consulta ===============

    /**
     * Lista todos os produtos.
     *
     * @return Lista imutável de produtos
     * @throws ServiceException se houver erro na operação
     */
    public List<Product> listAll() {
        logger.debug("Listando todos os produtos");

        try {
            List<Product> products = productRepository.listAll();
            logger.debug("Total de produtos encontrados: {}", products.size());

            return List.copyOf(products);
        } catch (RepositoryException e) {
            logger.error("Erro ao listar produtos", e);
            throw new ServiceException("Erro ao listar produtos", e);
        }
    }

    /**
     * Busca um produto por ID.
     *
     * @param id ID do produto
     * @return produto encontrado
     * @throws ProductException se o produto não existir
     * @throws ServiceException se houver erro na operação
     */
    public Product findById(int id) {
        logger.debug("Buscando produto por ID: {}", id);

        try {
            return ensureExists(id);
        } catch (RepositoryException | ProductException e) {
            logger.error("Erro ao buscar produto ID {}", id, e);
            throw new ServiceException("Erro ao buscar produto", e);
        }
    }

    //=============== Operações de estoque ===============

    /**
     * Adiciona quantidade ao estoque de um produto.
     *
     * @param id ID do produto
     * @param quantity quantidade a adicionar
     * @return produto atualizado
     * @throws ServiceException se houver erro na operação
     */
    public Product addStock(int id, int quantity) {
        logger.info("Adicionando {} unidades ao estoque do produto ID: {}", quantity, id);

        if(quantity < 0) {
            throw new ProductException(ERROR_INVALID_QUANTITY);
        }

        try {
            Product product = ensureExists(id);
            int newQuantity = product.getQuantity() + quantity;

            product.setQuantity(newQuantity);
            Product updated = productRepository.update(product);

            logger.info("Estoque atualizado. ID: {}, Nova quantidade: {}, Quantidade anterior: {}",
                    id, newQuantity, product.getQuantity() - quantity);

            return updated;
        } catch (Exception e) {
            logger.error("Erro ao adicionar estoque ao produto ID {}", id, e);
            throw new ServiceException("Erro ao adicionar estoque", e);
        }
    }

    /**
     * Remove quantidade do estoque de um produto.
     *
     * @param id ID do produto
     * @param quantity quantidade a remover
     * @return produto atualizado
     * @throws ProductException se não houver estoque suficiente
     * @throws ServiceException se houver erro na operação
     */
    public Product removeStock(int id, int quantity) {
        logger.info("Removendo {} unidades do estoque do produto ID: {}", quantity, id);

        if(quantity <= 0) {
            throw new ProductException(ERROR_INVALID_QUANTITY);
        }

        try {
            Product product = ensureExists(id);

            if(product.getQuantity() < quantity) {
                String error = String.format(ERROR_INSUFFICIENT_STOCK, product.getQuantity(), quantity);

                logger.warn(error);
                throw new ProductException(error);
            }

            int newQuantity = product.getQuantity() - quantity;
            product.setQuantity(newQuantity);

            Product updated = productRepository.update(product);

            logger.info("Estoque reduzido. ID: {}, Quantidade anterior: {}, Nova quantidade: {}",
                    id, product.getQuantity() - quantity, newQuantity);

            return updated;
        } catch (ProductException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao remover estoque do produto ID {}", id, e);
            throw new ServiceException("Erro ao remover estoque", e);
        }
    }

    /**
     * Atualiza o preco de um produto em estoque.
     *
     * @param id ID do produto
     * @param newPrice novo preço
     * @return produto atualizado
     * @throws ServiceException se houver erro na operação
     */
    public Product updatePrice(int id, BigDecimal newPrice) {
        logger.info("Atualizando preço do produto ID: {} para {}", id, newPrice);

        if(newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductException(ERROR_INVALID_PRICE);
        }

        try {
            Product product = ensureExists(id);
            BigDecimal oldPrice = product.getUnitValue();

            product.setUnitValue(newPrice);
            Product updated = productRepository.update(product);

            logger.info("Preço atualizado. ID: {}, Preço anterior: {}, Novo preço: {}",
                    id, product.getUnitValue(), oldPrice);

            return updated;
        } catch (Exception e) {
            logger.error("Erro ao atualizar o preço do produto ID {}", id, e);
            throw new ServiceException("Erro ao atualizar preço", e);
        }
    }

    //=============== Métodos auxiliares privados ===============

    /**
     * Verifica se um produto existe e o retorna.
     *
     * @param id ID do produto
     * @return produto encontrado
     * @throws ProductException se o produto não for encontrado
     */
    private Product ensureExists(int id) {
        logger.debug("Verificando existência do produto com ID: {}", id);

        return productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado com ID: {}", id);
                    return new ProductException("Produto com ID: " + id + " não existe");
                });
    }

    /**
     * Valida regras de negócio adicionais.
     *
     * @param product produto a validar
     * @throws ProductException se violar alguma regra
     */
    private void validateBusinessRules(Product product) {
        if(product.getQuantity() < 0) throw new ProductException("Quantidade não pode ser negotiva");
        if(product.getUnitValue().compareTo(BigDecimal.ZERO) < 0) throw new ProductException("Valor não pode ser negativo");
    }

    /**
     * Valida dados de atualização parcial.
     *
     * @param updates mapa de atualizações
     * @throws ProductException se os dados forem inválidos
     */
    private void validatePartialUpdate(Map<String, Object> updates) {
        if(updates.containsKey("quantidade")) {
            Object quantity = updates.get("quantidade");
            if(quantity instanceof Integer && (Integer) quantity < 0) {
                throw new ServiceException("Quantidade não pode ser negativa");
            }
        }

        if(updates.containsKey("valor_unitario")) {
            Object value = updates.get("valor_unitario");
            if(value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("Valor unitario não pode ser negativo");
            }
        }
    }
}
