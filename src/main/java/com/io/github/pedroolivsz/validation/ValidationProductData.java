package com.io.github.pedroolivsz.validation;

public enum ValidationProductData {

    SUCESSO("Sucesso"),
    NOME_INVALIDO("Nome inválido"),
    QUANTIDADE_INVALIDA("Quantidade inválida"),
    VALOR_INVALIDO("Valor inválido");

    private final String message;

    ValidationProductData(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
