package com.io.github.pedroolivsz.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.io.github.pedroolivsz.config.Database;
import com.io.github.pedroolivsz.dominio.Produto;
import com.io.github.pedroolivsz.rowMapper.ProdutoRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProdutoRepository {

    private static final Logger log = LoggerFactory.getLogger(ProdutoRepository.class);

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
            preparedStatement.setString(2, produto.getNome());
            preparedStatement.setBigDecimal(3, produto.getValorUnitario());
            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()){
                if(keys.next()) {
                    produto.setId(keys.getInt(1));
                }
            }

        } catch(SQLException sqlException) {
            log.error("Erro ao criar produto no banco. SQL: {} | Produto: {}", INSERT, produto, sqlException);
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
            preparedStatement.executeUpdate();

        } catch (SQLException sqlException) {
            log.error("Erro ao editar produto no banco de dados. SQL: {} | Produto: {}", UPDATE, produto);
            throw new RepositoryException("Erro ao editar produto. Tente novamente mais tarde");
        }

    }

    public void remove(int id) {

        try(Connection conn = Database.connect();
        PreparedStatement preparedStatement = conn.prepareStatement(DELETE)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException sqlException) {
            throw new RepositoryException("Erro ao deletar o produto.", sqlException);
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

            return produtos;
        } catch (SQLException sqlException) {
            throw new RepositoryException("Erro ao listar produtos.", sqlException);
        }

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
            throw new RepositoryException("Erro ao buscar o produto.", sqlException);
        }

        return Optional.empty();

    }

}
