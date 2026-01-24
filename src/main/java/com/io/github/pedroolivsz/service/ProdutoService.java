package com.io.github.pedroolivsz.service;

import java.math.BigDecimal;
import java.util.List;

import com.io.github.pedroolivsz.dominio.Produto;
import com.io.github.pedroolivsz.repository.ProdutoRepository;
import com.io.github.pedroolivsz.validation.ProductException;
import com.io.github.pedroolivsz.validation.ValidationProductData;

public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto ensureExists(int id) {
        return produtoRepository.findById(id).orElseThrow(() -> new ProductException("O produto não existe"));
    }

    public void ensureValid(Produto produto) {
        ValidationProductData validation = validarProduto(produto);
        if(validation != ValidationProductData.SUCESSO) {
            throw new ProductException(validation.getMessage());
        }
    }

    private ValidationProductData validarProduto(Produto produto) {
        if(produto.getNome() == null || produto.getNome().isBlank()) {
            return ValidationProductData.NOME_INVALIDO;
        }
        if(produto.getQuantidade() <= 0) {
            return ValidationProductData.QUANTIDADE_INVALIDA;
        }
        if(produto.getValorUnitario() == null || produto.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationProductData.VALOR_INVALIDO;
        }
        return ValidationProductData.SUCESSO;
    }

    public void create(String nome, int quantidade, BigDecimal valorUnitario) {
        Produto produto = new Produto(nome, quantidade, valorUnitario);
        ensureValid(produto);
        produtoRepository.create(produto);
    }

    public void update(int id, String nome, int quantidade, BigDecimal valorUnitario) {
        Produto produto = new Produto(id, nome, quantidade, valorUnitario);
        ensureExists(produto.getId());
        ensureValid(produto);
        produtoRepository.update(produto);
    }

    public void delete(int id) {
        ensureExists(id);
        produtoRepository.delete(id);
    }

    public List<Produto> listAll() {
        return List.copyOf(produtoRepository.listAll());
    }

    public Produto findById(int id) {
        return ensureExists(id);
    }

}
