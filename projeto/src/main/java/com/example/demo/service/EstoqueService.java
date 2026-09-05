package com.example.demo.service;

import com.example.demo.entity.Produto;
import com.example.demo.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;

    public EstoqueService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Produto realizarVenda(Long produtoId, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade da venda deve ser maior que zero.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        int alterados = produtoRepository.baixarEstoque(produtoId, quantidade);

        if (alterados == 0) {
            // Reconsulta o estoque para informar o valor atual ao cliente.
            Produto atual = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
            throw new IllegalStateException(
                    "Estoque insuficiente. Estoque atual: " + atual.getQtd()
            );
        }

        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    }

    @Transactional
    public Produto atualizarEstoque(Long produtoId, Integer quantidade) {
        if (quantidade == null || quantidade < 0) {
            throw new IllegalArgumentException("A quantidade em estoque não pode ser negativa.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        produto.setQtd(quantidade);
        return produtoRepository.save(produto);
    }

    @Transactional(readOnly = true)
    public Produto consultarEstoque(Long produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    }
}
