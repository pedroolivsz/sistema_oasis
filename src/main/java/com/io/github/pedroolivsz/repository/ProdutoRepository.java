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

public class ProdutoRepository {

    //=============== Constantes ===============

    //Configurações
    private static final int DEFAULT_SIZE_PAGE = 20;
    private static final int MAX_BATCH_SIZE = 1000;
    private static final int QUERY_TIMEOUT_SECONDS = 30;

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
    private static final String EXISTS_BY_NAME =
            "SELECT COUNT(*) FROM produtos WHERE LOWER(nome) = ?";
    private static final String COUNT_ALL =
            "SELECT COUNT(*) FROM produtos";
    private static final String STATISTICS =
            "SELECT COUNT(*) as total, " +
            "COALESCE(SUM(quantidade), 0) as total_quantidade, " +
            "COALESCE(AVG(valor_unitario), 0) as preco_medio, " +
            "COALESCE(MIN(valor_unitario), 0) as preco_minimo, " +
            "COALESCE(MAX(valor_unitario), 0) as preco_maximo" +
            "FROM produtos";

    //=============== Dependências ===============

    private final LogDatabase logger = new LogDatabase(ProdutoRepository.class);
    private final ProdutoRowMapper produtoRowMapper = new ProdutoRowMapper();

    //=============== Métodos CRUD básicos ===============

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

    public Product createWithTransaction(Product product) {
        validateProduct(product);

        Connection conn;
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

            logger.info("Produto atualizado parcialmente. ID: ", id);

            return findById(id)
                    .orElseThrow(() -> new RepositoryException(ERROR_NOT_FOUND + " após a atualização"));
        } catch (SQLException sqlException) {
            logger.logDatabaseError("Atualização parcial", sql.toString(), updates, sqlException);
            throw new RepositoryException("Erro na atualização parcial", sqlException);
        }
    }

    public void delete(int id) {
        try(Connection conn = Database.connect();
        PreparedStatement preparedStatement = conn.prepareStatement(DELETE)) {

            preparedStatement.setInt(1, id);
            int rows = preparedStatement.executeUpdate();

            if(rows == 0) {
                throw new RepositoryException("Produto não encontrado para remoção.");
            }
        } catch (SQLException sqlException) {
            logger.logDatabaseError("Remover o produto do banco de dados", DELETE, id, sqlException);
            throw new RepositoryException("Erro ao deletar o produto. Tente novamente mais tarde.");
        }
    }

    public List<Product> listAll() {
        List<Product> products = new ArrayList<>();

        try(Connection conn = Database.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(LIST_ALL);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            while(resultSet.next()) {
                products.add(produtoRowMapper.map(resultSet));
            }
        } catch (SQLException sqlException) {
            logger.logDatabaseError("Listar os produtos do banco de dados", LIST_ALL, sqlException);
            throw new RepositoryException("Erro ao listar produtos. Tente novamente mais tarde.");
        }

        return products;
    }

    public Optional<Product> findById(int id) {
        try(Connection conn = Database.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(FIND_BY_ID)) {

            preparedStatement.setInt(1, id);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    return Optional.of(produtoRowMapper.map(resultSet));
                }
            }
        } catch (SQLException sqlException) {
            logger.logDatabaseError("Procurar o produto por id no banco", FIND_BY_ID, id, sqlException);
            throw new RepositoryException("Erro ao buscar o produto. Tente novamente mais tarde.");
        }

        return Optional.empty();
    }

    //=============== Métodos auxiliares privados ===============

    private void setProductParameters(PreparedStatement preparedStatement, Product product) throws SQLException {
        preparedStatement.setInt(1, product.getQuantity());
        preparedStatement.setString(2, product.getName());
        preparedStatement.setBigDecimal(3, product.getUnitValue());
    }

    private void validateProduct(Product product) {
        if(product == null) throw new IllegalArgumentException("Produto não pode ser nulo");
        if(product.getName() == null || product.getName().trim().isEmpty()) throw new IllegalArgumentException("Nome do produto não pode ser nulo ou vazio");
        if(product.getQuantity() < 0) throw new IllegalArgumentException("Quantidade não pode ser nergativa");
        if(product.getUnitValue() == null || product.getUnitValue().signum() < 0) throw new IllegalArgumentException("Valor unitário deve ser não-nulo e não-negativo");
    }

    private void validateId(int id) {
        if(id < 0) throw new IllegalArgumentException("ID deve ser maior que zero");
    }

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
}
