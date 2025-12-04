package com.io.github.pedroolivsz.controller;

import com.io.github.pedroolivsz.dominio.Produto;
import com.io.github.pedroolivsz.service.ProdutoService;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    public void create(String nome, int quantidade, BigDecimal valorUnitario) {
        produtoService.create(nome, quantidade, valorUnitario);
    }

    public void update(int id, String nome, int quantidade, BigDecimal valorUnitario) {
        produtoService.update(id, nome, quantidade, valorUnitario);
    }

    public void delete(int id) {
        produtoService.delete(id);
    }

    public List<Produto> listAll() {
        return produtoService.listAll();
    }

    public Produto findbyId(int id) {
        return produtoService.findById(id);
    }
}
