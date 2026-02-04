package com.io.github.pedroolivsz.dominio;

public class Mesa {

    private int numeroMesa;
    private OrderTab orderTab;
    
    public Mesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
        this.orderTab = new OrderTab();
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public OrderTab getComanda() {
        return orderTab;
    }

    public void setComanda(OrderTab orderTab) {
        this.orderTab = orderTab;
    }

}
