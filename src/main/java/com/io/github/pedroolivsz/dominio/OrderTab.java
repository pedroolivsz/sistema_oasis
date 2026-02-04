package com.io.github.pedroolivsz.dominio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderTab {

    private int id;
    private int tableNumber;
    private List<Product> products;
    private BigDecimal totalAmount;

    public OrderTab(int id, int tableNumber, BigDecimal totalAmount) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.products = new ArrayList<>();
        this.totalAmount = totalAmount;
    }

    public OrderTab() {
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<Product> getProdutos() {
        return products;
    }

    public void setProdutos(List<Product> products) {
        this.products = products;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    

}
