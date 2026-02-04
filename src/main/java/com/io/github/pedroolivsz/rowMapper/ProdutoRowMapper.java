package com.io.github.pedroolivsz.rowMapper;

import com.io.github.pedroolivsz.dominio.Product;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProdutoRowMapper implements RowMapper<Product>{
    @Override
    public Product map(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setQuantity(resultSet.getInt("quantity"));
        product.setName(resultSet.getString("name"));
        BigDecimal valor = resultSet.getBigDecimal("unit_value");
        product.setUnitValue(valor != null ? valor : BigDecimal.ZERO);

        return product;
    }
}
