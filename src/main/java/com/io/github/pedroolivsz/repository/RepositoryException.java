package com.io.github.pedroolivsz.repository;

public class RepositoryException extends RuntimeException {
    public RepositoryException(String message, Throwable causa) {
        super(message, causa);
    }
    public RepositoryException(String message) { super(message); }
}
