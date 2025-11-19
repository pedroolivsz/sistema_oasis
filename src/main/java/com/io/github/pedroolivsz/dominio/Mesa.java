package com.io.github.pedroolivsz.dominio;

public class Mesa {

    private int numeroMesa;
    private Comanda comanda;
    
    public Mesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
        this.comanda = new Comanda();
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

}
