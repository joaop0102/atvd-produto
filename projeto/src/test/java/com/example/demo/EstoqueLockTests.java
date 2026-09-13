package com.example.demo;

import com.example.demo.entity.Produto;
import com.example.demo.repository.ProdutoRepository;
import com.example.demo.service.EstoqueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EstoqueLockTests {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private EstoqueService service;

    @BeforeEach
    void limpar() {
        repository.deleteAll();
    }

    @Test
    void deveUsarLockPessimistaNaQueryDeVenda() throws Exception {
        var method = ProdutoRepository.class.getMethod("findByIdWithLock", Long.class);
        var lock = method.getAnnotation(org.springframework.data.jpa.repository.Lock.class);

        assertNotNull(lock, "A query de venda precisa ter @Lock");
        assertEquals(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE, lock.value());
    }

    @Test
    void duasVendasConcorrentesNaoPodemConsumirAMesmaUnidade() throws Exception {
        Produto produto = repository.saveAndFlush(new Produto("Produto Lock", 1));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch inicio = new CountDownLatch(1);

        Callable<VendaResult> venda = () -> {
            inicio.await(5, TimeUnit.SECONDS);
            try {
                service.realizarVenda(produto.getId(), 1);
                return new VendaResult(true, null);
            } catch (RuntimeException ex) {
                // Dependendo do momento em que a segunda transacao tenta obter
                // o lock, o banco pode devolver a excecao da aplicacao
                // (estoque insuficiente) ou a excecao de bloqueio/timeout.
                return new VendaResult(false, ex);
            }
        };

        var f1 = executor.submit(venda);
        var f2 = executor.submit(venda);
        inicio.countDown();

        VendaResult resultado1 = f1.get(15, TimeUnit.SECONDS);
        VendaResult resultado2 = f2.get(15, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertNotEquals(resultado1.sucesso(), resultado2.sucesso(),
                "Exatamente uma venda deve ser aprovada e a outra recusada pelo controle de concorrencia");

        VendaResult recusada = resultado1.sucesso() ? resultado2 : resultado1;
        assertNotNull(recusada.erro(), "A segunda tentativa precisa ter uma causa de recusa");

        Produto finalizado = repository.findById(produto.getId()).orElseThrow();
        assertEquals(0, finalizado.getQtd(), "O estoque final deve ser zero");
    }

    private record VendaResult(boolean sucesso, RuntimeException erro) {
    }
}
