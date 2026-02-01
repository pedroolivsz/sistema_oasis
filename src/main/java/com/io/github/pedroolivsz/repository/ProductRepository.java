package com.io.github.pedroolivsz.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.io.github.pedroolivsz.config.Database;
import com.io.github.pedroolivsz.dominio.Product;
import com.io.github.pedroolivsz.logs.LogDatabase;
import com.io.github.pedroolivsz.rowMapper.ProdutoRowMapper;

/**
 * Repository responsável pelas operações de persistência de produtos.
 *
 * <p>Esta classe oferece métodos CRUD completos</p>
 *
 * <p>Características principais: </p>
 * <ul>
 *     <li>Operações CRUD completas com validalção</li>
 *     <li>Suporte a transações e rollback</li>
 *     <li>Logging detalhado de erros</li>
 * </ul>
 *
 * @author João Pedro
 */

public class ProductRepository {
    //=============== Constantes ===============

    //Mensagens de erro padronizadas
    private static final String ERROR_CREATE = "Erro ao criar produto";
    private static final String ERROR_UPDATE = "Erro ao atualizar produto";
    private static final String ERROR_DELETE = "Erro ao deletar produto";
    private static final String ERROR_LIST = "Erro ao listar produtos";
    private static final String ERROR_FIND = "Erro ao procurar produto";
    private static final String ERROR_NOT_FOUND = "Produto não encontrado";

    //Queries SQL
    private static final String INSERT =
            "INSERT INTO produtos (quantidade, nome, valor_unitario) VALUES(?, ?, ?)";
    private static final String UPDATE =
            "UPDATE produtos SET quantidade = ?, nome = ?, valor_unitario = ? WHERE id = ?";
    private static final String DELETE =
            "DELETE FROM produtos WHERE id = ?";
    private static final String LIST_ALL =
            "SELECT id, quantidade, nome, valor_unitario FROM produtos ORDER BY id";
    private static final String FIND_BY_ID =
            "SELECT id, quantidade, nome, valor_unitario FROM produtos WHERE id = ?";

    //=============== Dependências ===============

    private final LogDatabase logger = new LogDatabase(ProductRepository.class);
    private final ProdutoRowMapper produtoRowMapper = new ProdutoRowMapper();

    //=============== Métodos CRUD básicos ===============

    /**
     * Cria um novo produto no banco de dados.
     *
     * @param product o produto a ser criado (não pode ser null)
     * @return o produto criado com o ID gerado
     * @throws RepositoryException se houver erro na operação
     * @throws IllegalArgumentException se o produto for null ou inválido
     */

    public Product create(Product product) {
        validateProduct(product);
        try(Connection conn = Database.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            setProductParameters(preparedStatement, product);

            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()){
                if(keys.next()) {
                    product.setId(keys.getInt(1));
                }
            }

            logger.info("Produto criado com sucesso. ID: " + product.getId());
        } catch(SQLException sqlException) {
            logger.logDatabaseError("Criar produto no banco", INSERT, product, sqlException);
            throw new RepositoryException(ERROR_CREATE + ". Tente novamente mais tarde");
        }

        return product;
    }

    /**
     * Cria um produto dentro de uma transação gerenciada.
     *
     * @param product o produto a ser criado
     * @return o produto criado com o ID gerado
     * @throws RepositoryException se houver erro na operação
     * @throws IllegalArgumentException se o produto for null ou inválido
     */

    public Product createWithTransaction(Product product) {
        validateProduct(product);

        Connection conn = null;
        try {
            conn = Database.connect();
            conn.setAutoCommit(false);

            try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)){
                setProductParameters(preparedStatement, product);

                preparedStatement.executeUpdate();

                try (ResultSet key = preparedStatement.getGeneratedKeys()) {
                    if(key.next()) product.setId(key.getInt(1));
                }
            }

            conn.commit();
            logger.info("Produto criado com sucesso (Transação). ID: " + product.getId());
            return product;
        } catch (SQLException sqlException) {
            rollback(conn);
            logger.logDatabaseError("Criar produto com transição", INSERT, product, sqlException);
            throw new RepositoryException(ERROR_CREATE + " (Transação)", sqlException);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Atualiza um produto existente no banco de dados.
     *
     * @param product o produto com os dados atualizados
     * @return o produto atualizado
     * @throws RepositoryException se houver erro na operação
     * @throws IllegalArgumentException se o produto for null ou inválido ou se o id não existir
     */

    public Product update(Product product) {
        validateProduct(product);
        validateId(product.getId());

        try(Connection conn = Database.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(UPDATE)) {
            setProductParameters(preparedStatement, product);

            preparedStatement.setInt(4, product.getId());

            int rows = preparedStatement.executeUpdate();

            if(rows == 0) {
                throw new RepositoryException(ERROR_NOT_FOUND + " para atualização. ID: " + product.getId());
            }

            logger.info("Produto atualizado com sucesso. ID: " + product.getId());
        } catch (SQLException sqlException) {
            logger.logDatabaseError("Editar produto no banco de dados", UPDATE, product, sqlException);
            throw new RepositoryException(ERROR_UPDATE + ". Tente novamente mais tarde", sqlException);
        }

        return product;
    }

    /**
     * Atualiza parcialmente um produto existente.
     * Permite atualizar somente compos específicos.
     *
     * @param id o ID do produto
     * @param updates mapa com os campos a serem atualizados
     * @return o produto atualizado
     * @throws RepositoryException se houver erro na operação
     * @throws IllegalArgumentException se não houver atualizações ou o ID for inválido
     */

    public Product partialUpdate(int id, Map<String, Object> updates) {
        if(updates == null || updates.isEmpty()) throw new IllegalArgumentException("Nenhuma atualizaçao fornecida");

        validateId(id);

        StringBuilder sql = new StringBuilder("UPDATE produtos SET");
        List<Object> params = new ArrayList<>();

        updates.forEach((key, value) -> {
            sql.append(key).append(" = ?, ");
            params.add(value);
        });

        sql.setLength(sql.length() - 2); //Remove a última vírgula
        sql.append(" WHERE id = ?");
        params.add(id);

        try (Connection conn = Database.connect();
        PreparedStatement preparedStatement = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }

            int rows = preparedStatement.executeUpdate();

            if(rows == 0) {
                throw new RepositoryException(ERROR_NOT_FOUND + ". ID: " + id);
            }

            logger.info("Produto atualizado parcialmente. ID: " + id);

            return findById(id)
                    .orElseThrow(() -> new RepositoryException(ERROR_NOT_FOUND + " após a atualização"));
        } catch (SQLException sqlException) {
            logger.logDatabaseError("Atualização parcial", sql.toString(), updates, sqlException);
            throw new RepositoryException("Erro na atualização parcial", sqlException);
        }
    }

    /**
     * Remove um produto do banco de dados.
     *
     * @param id o ID do produto a ser removido
     * @throws RepositoryException se houver erro na operação ou se o produto não existir
     * @throws IllegalArgumentException se o ID do produto for inválido
     */

    public void delete(int id) {
        validateId(id);

        try(Connection conn = Database.connect();
        PreparedStatement preparedStatement = conn.prepareStatement(DELETE)) {

            preparedStatement.setInt(1, id);
            int rows = preparedStatement.executeUpdate();

            if(rows == 0) {
                throw new RepositoryException(ERROR_NOT_FOUND + " para remoção. ID: " + id);
            }
        } catch (SQLException sqlException) {
            logger.logDatabaseError("Remover o produto do banco de dados", DELETE, id, sqlException);
            throw new RepositoryException(ERROR_DELETE + ". Tente novamente mais tarde.", sqlException);
        }
    }

    /**
     * Lista todos os produtos do banco de dados.
     *
     * @return lista com todos os produtos
     * @throws RepositoryException se houver um erro na operação
     */

    public List<Product> listAll() {
        List<Product> products = new ArrayList<>();

        try(Connection conn = Database.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(LIST_ALL);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            while(resultSet.next()) {
                products.add(produtoRowMapper.map(resultSet));
            }

            logger.info("Listados " + products.size() + " produtos");
        } catch (SQLException sqlException) {
            logger.logDatabaseError("Listar os produtos do banco de dados", LIST_ALL, sqlException);
            throw new RepositoryException(ERROR_LIST + ". Tente novamente mais tarde.", sqlException);
        }

        return products;
    }

    /**
     * Buscar um produto pelo ID
     *
     * @param id o ID do produto
     * @return optional contendo o produto caso encontrado, vazio caso contrário
     * @throws RepositoryException se houver erro na operação
     * @throws IllegalArgumentException se o ID for inválido
     */

    public Optional<Product> findById(int id) {
        validateId(id);

        try(Connection conn = Database.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(FIND_BY_ID)) {

            preparedStatement.setInt(1, id);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    Product product = produtoRowMapper.map(resultSet);
                    logger.info("Produto encontrado. ID: " + product.getId());
                    return Optional.of(product);
                }
            }
        } catch (SQLException sqlException) {
            logger.logDatabaseError("Procurar o produto por id no banco", FIND_BY_ID, id, sqlException);
            throw new RepositoryException(ERROR_FIND + ". Tente novamente mais tarde.");
        }

        logger.info("Produto não encontrado. ID: " + id);
        return Optional.empty();
    }

    //=============== Métodos auxiliares privados ===============

    /**
     * Define os parâmetros do produto no preparedStatement.
     *
     * @param preparedStatement a instância do preparedStatement
     * @param product o produto contendo os parâmetros
     * @throws SQLException se houver erro na operação
     */

    private void setProductParameters(PreparedStatement preparedStatement, Product product) throws SQLException {
        preparedStatement.setInt(1, product.getQuantity());
        preparedStatement.setString(2, product.getName());
        preparedStatement.setBigDecimal(3, product.getUnitValue());
    }

    /**
     * Valida se o produto é válido para as operações de banco.
     *
     * @param product o produto a ser validado
     * @throws IllegalArgumentException se algum campo do produto for inválido
     */

    private void validateProduct(Product product) {
        if(product == null) throw new IllegalArgumentException("Produto não pode ser nulo");
        if(product.getName() == null || product.getName().trim().isEmpty()) throw new IllegalArgumentException("Nome do produto não pode ser nulo ou vazio");
        if(product.getQuantity() < 0) throw new IllegalArgumentException("Quantidade não pode ser nergativa");
        if(product.getUnitValue() == null || product.getUnitValue().signum() < 0) throw new IllegalArgumentException("Valor unitário deve ser não-nulo e não-negativo");
    }

    /**
     * Valida se o ID do produto é válido para as operações de banco.
     *
     * @param id o ID a ser validado
     * @throws IllegalArgumentException se o ID for inválido
     */

    private void validateId(int id) {
        if(id < 0) throw new IllegalArgumentException("ID deve ser maior que zero");
    }

    /**
     * Executa rollback em uma conexão.
     *
     * @param conn conexão passada por parâmetro
     */

    private void rollback(Connection conn) {
        if(conn != null) {
            try {
                conn.rollback();
                logger.info("Rollback executado com sucesso");
            } catch (SQLException sqlException) {
                logger.logDatabaseError("Rollback falhou", "", null, sqlException);
            }
        }
    }

    /**
     * Fecha uma conexão de forma segura.
     *
     * @param conn conexão passada por parâmetro
     */

    private void closeConnection(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException sqlException) {
                logger.logDatabaseError("Erro ao fechar conexão", "", null, sqlException);
            }
        }
    }
}
