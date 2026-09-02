package com.example.demo;

import com.example.demo.entity.Produto;
import com.example.demo.repository.ProdutoRepository;
import com.example.demo.service.EstoqueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EstoqueServiceTests {

    @Autowired
    ProdutoRepository repository;

    @Autowired
    EstoqueService service;

    @Test
    void deveDarBaixaNoEstoque() {
        Produto produto = repository.save(new Produto("Produto Teste", 10));

        Produto atualizado = service.realizarVenda(produto.getId(), 3);

        assertEquals(7, atualizado.getQtd());
    }

    @Test
    void naoDevePermitirVendaMaiorQueEstoque() {
        Produto produto = repository.save(new Produto("Produto Teste", 2));

        assertThrows(IllegalStateException.class,
                () -> service.realizarVenda(produto.getId(), 3));
    }
}
