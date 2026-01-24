package com.io.github.pedroolivsz.viewfx;

import com.io.github.pedroolivsz.controller.ProdutoController;
import com.io.github.pedroolivsz.dominio.Produto;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.util.List;


public class ProductListView {
    private final ProdutoController produtoController;
    private final BorderPane root;
    private final TableView<Produto> tabela;

    public ProductListView(ProdutoController produtoController) {
        this.produtoController = produtoController;
        this.root = new BorderPane();
        this.tabela = new TableView<>();

        setupTable();
        loadData();

        root.setCenter(tabela);
    }

    private void setupTable() {
        TableColumn<Produto, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getId()).asObject());
        colunaId.setPrefWidth(50);

        TableColumn<Produto, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getNome()));
        colunaNome.setPrefWidth(200);

        TableColumn<Produto, Integer> colunaQuantidade = new TableColumn<>("Quantidade");
        colunaQuantidade.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getQuantidade()).asObject());
        colunaQuantidade.setPrefWidth(120);

        tabela.getColumns().addAll(colunaId, colunaNome, colunaQuantidade);
    }

    private void loadData() {
        List<Produto> produtos = produtoController.listAll();
        tabela.setItems(FXCollections.observableList(produtos));
    }

    public Parent getRoot() {
        return root;
    }
}
