package com.io.github.pedroolivsz.repository;

import com.io.github.pedroolivsz.logs.LogDatabase;

/**
 * Repository responsável pelas operações de persistencia de comandas.
 *
 * <p>Esta classe oferece métodos CRUD completos</p>
 *
 * <p>Características principais: </p>
 * <ul>
 *     <li>Operações CRUD completas com validações</li>
 *     <li>Logging detalhado de erros</li>
 * </ul>
 *
 * @author João Pedro
 */

public class OrderTabRepository {
    private final LogDatabase logger = new LogDatabase(OrderTabRepository.class);

}
