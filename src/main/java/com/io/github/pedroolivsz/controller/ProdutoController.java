package com.io.github.pedroolivsz.controller;

import com.io.github.pedroolivsz.dominio.Produto;
import com.io.github.pedroolivsz.service.ProdutoService;
import com.io.github.pedroolivsz.validation.ProductException;
import com.io.github.pedroolivsz.validation.ProdutoValidation;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;

public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    public ProdutoValidation create(String nome, int quantidade, BigDecimal valorUnitario) {
        Produto produto = new Produto(quantidade, nome, valorUnitario);
        ProdutoValidation produtoValidation = produtoService.create(produto);
        if(produtoValidation.equals(ProdutoValidation.NOME_INVALIDO)) {
            throw new IllegalArgumentException("Erro: Nome inválido");
        } else if(produtoValidation.equals(ProdutoValidation.QUANTIDADE_INVALIDA)) {
            throw new IllegalArgumentException("Erro: Quantidade inválida");
        } else if(produtoValidation.equals(ProdutoValidation.VALOR_IGUAL_ZERO)) {
            throw new IllegalArgumentException("Erro: O valor não pode ser igual a zero");
        } else if(produtoValidation.equals(ProdutoValidation.VALOR_NEGATIVO)) {
            throw new IllegalArgumentException("Erro: O valor não pode ser negativo");
        }
        return produtoValidation;
    }

    public ProdutoValidation update(int id, String nome, int quantidade, BigDecimal valorUnitario) {
        Produto produto = new Produto(id, quantidade, nome, valorUnitario);
        ProdutoValidation produtoValidation = produtoService.update(produto);

        if(produtoValidation.equals(ProdutoValidation.PRODUTO_NAO_EXISTE)) {
            throw new ProductException("Erro: O produto não existe");
        } if(produtoValidation.equals(ProdutoValidation.NOME_INVALIDO)) {
            throw new IllegalArgumentException("Erro: Nome inválido");
        } if(produtoValidation.equals(ProdutoValidation.QUANTIDADE_INVALIDA)) {
            throw new IllegalArgumentException("Erro: Quantidade inválida");
        } if(produtoValidation.equals(ProdutoValidation.VALOR_IGUAL_ZERO)) {
            throw new IllegalArgumentException("Erro: O valor não pode ser igual a zero");
        } if(produtoValidation.equals(ProdutoValidation.VALOR_NEGATIVO)) {
            throw new IllegalArgumentException("Erro: O valor não pode ser negativo");
        }
        return produtoValidation;
    }

    public ProdutoValidation delete(int id) {
        ProdutoValidation produtoValidation = produtoService.delete(id);
        if(produtoValidation.equals(ProdutoValidation.PRODUTO_NAO_EXISTE)) {
            throw new ProductException("Erro: O produto não existe");
        }
        return produtoValidation;
    }

    public String listAll() {
        return produtoService.listAll();
    }

    public Produto findbyId(int id) {
        Produto produto = produtoService.findById(id);
        if(produto == null) {
            throw new ProductException("Erro: O produto não existe");
        }
        return produto;
    }
}
