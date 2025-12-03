package com.io.github.pedroolivsz.testes;

import com.io.github.pedroolivsz.controller.ProdutoController;
import com.io.github.pedroolivsz.repository.ProdutoRepository;
import com.io.github.pedroolivsz.service.ProdutoService;
import com.io.github.pedroolivsz.view.ProdutoView;

public class TesteProdutoRepository {

    public static void main(String[] args) {
        ProdutoRepository produtoRepository = new ProdutoRepository();
        ProdutoService produtoService = new ProdutoService(produtoRepository);
        ProdutoController produtoController = new ProdutoController(produtoService);
        ProdutoView produtoView = new ProdutoView(produtoController);

        //produtoView.createProduct();
        //produtoView.updateProduct();
        //produtoView.deleteProduct();
        //produtoView.listAllProducts();
        produtoView.findProductById();
    }

}
