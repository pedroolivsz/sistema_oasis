package com.io.github.pedroolivsz;

import com.io.github.pedroolivsz.controller.ProdutoController;
import com.io.github.pedroolivsz.repository.ProdutoRepository;
import com.io.github.pedroolivsz.service.ProdutoService;
import com.io.github.pedroolivsz.viewfx.ProductListView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainFX extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ProdutoRepository produtoRepository = new ProdutoRepository();
        ProdutoService produtoService = new ProdutoService(produtoRepository);
        ProdutoController produtoController = new ProdutoController(produtoService);
        BorderPane borderPane = new BorderPane();
        ProductListView view = new ProductListView(produtoController, borderPane);
        Scene scene = new Scene(view.getBorderPane(), 600, 400);

        stage.setTitle("Lista produtos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
