package com.io.github.pedroolivsz.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.io.github.pedroolivsz.config.Database;
import com.io.github.pedroolivsz.dominio.Produto;
import com.io.github.pedroolivsz.logs.LogDatabase;
import com.io.github.pedroolivsz.rowMapper.ProdutoRowMapper;

public class ProdutoRepository {


    private final LogDatabase logger = new LogDatabase(ProdutoRepository.class);

    private final ProdutoRowMapper produtoRowMapper = new ProdutoRowMapper();

    private static final String INSERT = "INSERT INTO produtos (quantidade, nome, valor_unitario) VALUES(?, ?, ?)";
    private static final String UPDATE = "UPDATE produtos SET quantidade = ?, nome = ?, valor_unitario = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM produtos WHERE id = ?";
    private static final String LIST_ALL = "SELECT id, quantidade, nome, valor_unitario FROM produtos ORDER BY id";
    private static final String FIND_BY_ID = "SELECT id, quantidade, nome, valor_unitario FROM produtos WHERE id = ?";

    public void create(Produto produto) {

        try(Connection conn = Database.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, produto.getQuantidade());
            preparedStatement.setString(2, produto.getNome().toLowerCase());
            preparedStatement.setBigDecimal(3, produto.getValorUnitario());
            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()){
                if(keys.next()) {
                    produto.setId(keys.getInt(1));
                }
            }

        } catch(SQLException sqlException) {
            logger.logDatabaseError("Criar produto no banco", INSERT, produto, sqlException);
            throw new RepositoryException("Erro ao criar o produto. Tente novamente mais tarde");
        }

    }

    public void update(Produto produto) {

        try(Connection conn = Database.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(UPDATE)) {

            preparedStatement.setInt(1, produto.getQuantidade());
            preparedStatement.setString(2, produto.getNome());
            preparedStatement.setBigDecimal(3, produto.getValorUnitario());
            preparedStatement.setInt(4, produto.getId());
            int rows = preparedStatement.executeUpdate();

            if(rows == 0) {
                throw new RepositoryException("Produto não encontrado para atualização.");
            }

        } catch (SQLException sqlException) {
            logger.logDatabaseError("Editar produto no banco de dados", UPDATE, produto, sqlException);
            throw new RepositoryException("Erro ao editar produto. Tente novamente mais tarde");
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

    public List<Produto> listAll() {

        List<Produto> produtos = new ArrayList<>();

        try(Connection conn = Database.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(LIST_ALL);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            while(resultSet.next()) {
                produtos.add(produtoRowMapper.map(resultSet));
            }

        } catch (SQLException sqlException) {
            logger.logDatabaseError("Listar os produtos do banco de dados", LIST_ALL, sqlException);
            throw new RepositoryException("Erro ao listar produtos. Tente novamente mais tarde.");
        }

        return produtos;

    }

    public Optional<Produto> findById(int id) {

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

}
