package com.example.demo.event;

import java.time.Instant;

/**
 * Evento publicado sempre que uma operacao altera os dados do produto/estoque.
 */
public record ProdutoAlteradoEvent(
        Long produtoId,
        String nome,
        Integer quantidade,
        String operacao,
        Instant dataHora
) {
}
