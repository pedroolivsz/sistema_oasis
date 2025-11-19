package com.io.github.pedroolivsz.testes;

import com.io.github.pedroolivsz.dominio.Produto;
import com.io.github.pedroolivsz.repository.ProdutoRepository;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class TesteProdutoRepository {

    public static void main(String[] args) {
        ProdutoRepository produtoRepository = new ProdutoRepository();
        /*Produto produtoTest = new Produto();
        String nome = JOptionPane.showInputDialog("nome: ");
        int qtd = Integer.parseInt(JOptionPane.showInputDialog("Qtd: "));
        BigDecimal valor = BigDecimal.valueOf(Double.parseDouble(JOptionPane.showInputDialog("Valor")));

        produtoTest.setNome(nome);
        produtoTest.setQuantidade(qtd);
        produtoTest.setValorUnitario(valor);

        produtoRepository.create(produtoTest);*/

        /*int id = Integer.parseInt(JOptionPane.showInputDialog("id: "));
        produtoRepository.remove(id);*/

        /*List<Produto> produtos = produtoRepository.listAll();

        StringBuilder lista = new StringBuilder();
        for (Produto p : produtos) {
            lista.append(p);
        }

        JOptionPane.showMessageDialog(null, lista);*/

        int id = Integer.parseInt(JOptionPane.showInputDialog("id: "));
        Optional<Produto> findProd = produtoRepository.findById(id);

        JOptionPane.showMessageDialog(null, findProd);

        /*int id = Integer.parseInt(JOptionPane.showInputDialog("id: "));
        String nome = JOptionPane.showInputDialog("nome: ");
        int qtd = Integer.parseInt(JOptionPane.showInputDialog("Qtd: "));
        BigDecimal valor = BigDecimal.valueOf(Double.parseDouble(JOptionPane.showInputDialog("Valor")));

        Produto produtoTestUpdate = new Produto();
        produtoTestUpdate.setId(id);
        produtoTestUpdate.setNome(nome);
        produtoTestUpdate.setQuantidade(qtd);
        produtoTestUpdate.setValorUnitario(valor);

        produtoRepository.update(produtoTestUpdate);*/
    }

}
