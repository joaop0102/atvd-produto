package com.example.demo.service;

import com.example.demo.entity.Produto;
import com.example.demo.event.ProdutoAlteradoEvent;
import com.example.demo.repository.ProdutoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EstoqueService(ProdutoRepository produtoRepository,
            ApplicationEventPublisher eventPublisher) {
        this.produtoRepository = produtoRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Produto realizarVenda(Long produtoId, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade da venda deve ser maior que zero.");
        }

        Produto produto = produtoRepository.findByIdWithLock(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));

        if (produto.getQtd() < quantidade) {
            throw new IllegalStateException(
                    "Estoque insuficiente. Estoque atual: " + produto.getQtd());
        }

        produto.setQtd(produto.getQtd() - quantidade);
        Produto atualizado = produtoRepository.save(produto);
        publicarEvento(atualizado, "VENDA");
        return atualizado;
    }

    @Transactional
    public Produto atualizarEstoque(Long produtoId, Integer quantidade) {
        if (quantidade == null || quantidade < 0) {
            throw new IllegalArgumentException("A quantidade em estoque nao pode ser negativa.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));

        produto.setQtd(quantidade);
        Produto atualizado = produtoRepository.save(produto);
        publicarEvento(atualizado, "ATUALIZACAO_ESTOQUE");
        return atualizado;
    }

    private void publicarEvento(Produto produto, String operacao) {
        eventPublisher.publishEvent(new ProdutoAlteradoEvent(
                produto.getId(),
                produto.getNome(),
                produto.getQtd(),
                operacao,
                Instant.now()));
    }

    @Transactional(readOnly = true)
    public Produto consultarEstoque(Long produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));
    }

        @Transactional
    public Produto criarProduto(Produto produto) {
        Produto novoProduto = produtoRepository.save(produto);
        publicarEvento(novoProduto, "CRIACAO");
        return novoProduto;
    }

    @Transactional
    public void deletarProduto(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));
        
        produtoRepository.delete(produto);
        publicarEvento(produto, "EXCLUSAO");
    }
}