package com.io.github.pedroolivsz.dominio;

import java.math.BigDecimal;

public class Produto {

    private int id;
    private int quantidade;
    private String nome;
    private BigDecimal valorUnitario;

    public Produto(int id, String nome, int quantidade, BigDecimal valorUnitario) {
        this.id = id;
        this.quantidade = quantidade;
        this.nome = nome;
        this.valorUnitario = valorUnitario;
    }

    public Produto(String nome, int quantidade, BigDecimal valorUnitario) {
        this.quantidade = quantidade;
        this.nome = nome;
        this.valorUnitario = valorUnitario;
    }

    public Produto() {

    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Id: " + id +
                "| " + nome +
                "| Quantidade: " + quantidade +
                "| Valor unitário: " + valorUnitario;
    }
}
