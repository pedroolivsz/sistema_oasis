package com.io.github.pedroolivsz.view;

import com.io.github.pedroolivsz.controller.ProdutoController;
import com.io.github.pedroolivsz.dominio.Produto;
import com.io.github.pedroolivsz.validation.ValidationProductData;

import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.util.List;

public class ProdutoView {
    private final ProdutoController produtoController;
    public ProdutoView(ProdutoController produtoController) {
        this.produtoController = produtoController;
    }
    public void createProduct() {
        String nome = JOptionPane.showInputDialog(null,
                "Nome:",
                "Cadastro de produto:",
                JOptionPane.PLAIN_MESSAGE);
        int quantidade = Integer.parseInt(JOptionPane.showInputDialog(null,
                "Quantidade:",
                "Cadastro de produto:",
                JOptionPane.PLAIN_MESSAGE));
        BigDecimal valorUnitario = BigDecimal.valueOf(Double.parseDouble(JOptionPane.showInputDialog(null,
                "Valor unitário:",
                "Cadastro de produto:",
                JOptionPane.PLAIN_MESSAGE)));
        produtoController.create(nome, quantidade, valorUnitario);
        JOptionPane.showMessageDialog(null, "Nome: " + nome + "\n" +
                "Quantidade: " + quantidade + "\n" +
                "Valor unitário: R$" + valorUnitario + "\n" +
                "Adicionado com sucesso ao estoque!");
    }
    public void updateProduct() {
        int id = Integer.parseInt(JOptionPane.showInputDialog(null,
                "Id do produto:",
                "Edição de produto",
                JOptionPane.PLAIN_MESSAGE));
        String nome = JOptionPane.showInputDialog(null,
                "Nome:",
                "Cadastro de produto:",
                JOptionPane.PLAIN_MESSAGE);
        int quantidade = Integer.parseInt(JOptionPane.showInputDialog(null,
                "Quantidade:",
                "Cadastro de produto:",
                JOptionPane.PLAIN_MESSAGE));
        BigDecimal valorUnitario = BigDecimal.valueOf(Double.parseDouble(JOptionPane.showInputDialog(null,
                "Valor unitário:",
                "Cadastro de produto:",
                JOptionPane.PLAIN_MESSAGE)));
        produtoController.update(id, nome, quantidade, valorUnitario);
        JOptionPane.showMessageDialog(null, "Nome: " + nome + "\n" +
                "Quantidade: " + quantidade + "\n" +
                "Valor unitário: R$" + valorUnitario + "\n" +
                "Editado com sucesso!");
    }

    public void deleteProduct() {
        int id = Integer.parseInt(JOptionPane.showInputDialog(null,
                "Id do produto:",
                "Deletar produto",
                JOptionPane.PLAIN_MESSAGE));
        produtoController.delete(id);
        JOptionPane.showMessageDialog(null, "O produto de ID: " + id +
                " foi excluido com sucesso", "Confirmação", JOptionPane.PLAIN_MESSAGE);
    }

    public void listAllProducts() {
        List<Produto> produtos = produtoController.listAll();
        if(produtos.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Lista vazia",
                    "Lista de produtos",
                    JOptionPane.PLAIN_MESSAGE);
            return;
        }
        StringBuilder listaProdutos = new StringBuilder();
        for(Produto produto : produtos) {
            listaProdutos.append(produto);
            listaProdutos.append("\n");
        }
        JOptionPane.showMessageDialog(null,
                listaProdutos,
                "Lista de produtos",
                JOptionPane.PLAIN_MESSAGE);
    }

    public void findProductById() {
        int id = Integer.parseInt(JOptionPane.showInputDialog(null,
                "Id do produto:",
                "Procurar produto por id",
                JOptionPane.PLAIN_MESSAGE));
        Produto produto = produtoController.findbyId(id);
        JOptionPane.showMessageDialog(null, produto,
                "Produto encontrado",
                JOptionPane.PLAIN_MESSAGE);
    }
}
