package com.io.github.pedroolivsz.rowMapper;

import com.io.github.pedroolivsz.dominio.OrderTab;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderTabRowMapper implements RowMapper<OrderTab> {
    @Override
    public OrderTab map(ResultSet rs) throws SQLException {
        OrderTab orderTab = new OrderTab();

        orderTab.setId(rs.getInt("id"));
        orderTab.setTableNumber(rs.getInt("table_number"));
        orderTab.setTotalAmount(rs.getBigDecimal("total_amount"));
        orderTab.setProdutos(new ArrayList<>());

        return orderTab;
    }
}
