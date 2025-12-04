package com.io.github.pedroolivsz.viewfx;

import com.io.github.pedroolivsz.controller.ProdutoController;
import com.io.github.pedroolivsz.dominio.Produto;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.util.List;


public class ProductListView {
    private final ProdutoController produtoController;
    private final BorderPane borderPane;

    public ProductListView(ProdutoController produtoController, BorderPane borderPane) {
        this.produtoController = produtoController;
        this.borderPane = borderPane;
        TableView<Produto> tabela = new TableView<>();
        TableColumn<Produto, Integer> colunaId = new TableColumn<>();
        colunaId.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getId()).asObject());
        tabela.getColumns().addAll(colunaId);
        List<Produto> produtos = produtoController.listAll();
        tabela.setItems(FXCollections.observableList(produtos));
        borderPane.setCenter(tabela);
    }

    public Parent getBorderPane() {
        return borderPane;
    }
}
