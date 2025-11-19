package com.io.github.pedroolivsz.rowMapper;

import com.io.github.pedroolivsz.dominio.Produto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProdutoRowMapper {

    public Produto map(ResultSet resultSet) throws SQLException {

        Produto produto = new Produto();
        produto.setId(resultSet.getInt("id"));
        produto.setQuantidade(resultSet.getInt("quantidade"));
        produto.setNome(resultSet.getString("nome"));
        BigDecimal valor = resultSet.getBigDecimal("valor_unitario");
        produto.setValorUnitario(valor != null ? valor : BigDecimal.ZERO);

        return produto;

    }
}
