package com.io.github.pedroolivsz;

import com.io.github.pedroolivsz.controller.ProdutoController;
import com.io.github.pedroolivsz.repository.ProdutoRepository;
import com.io.github.pedroolivsz.service.ProdutoService;
import com.io.github.pedroolivsz.viewfx.ProductListView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ProdutoRepository produtoRepository = new ProdutoRepository();
        ProdutoService produtoService = new ProdutoService(produtoRepository);
        ProdutoController produtoController = new ProdutoController(produtoService);
        ProductListView view = new ProductListView(produtoController);
        Scene scene = new Scene(view.getRoot(), 600, 400);

        stage.setTitle("Lista produtos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
