package com.io.github.pedroolivsz.validation;

import com.io.github.pedroolivsz.dominio.Product;

import java.math.BigDecimal;

public class ProductValidator {
    public static void validateProduct(Product product) {
        if(product.getName() == null || product.getName().isBlank()) throw new ProductException("Invalid name");
        if(product.getQuantity() <= 0) throw new ProductException("Invalid quantity");
        if(product.getUnitValue() == null || product.getUnitValue().compareTo(BigDecimal.ZERO) <= 0) throw new ProductException("Invalid unit value");
    }
}
