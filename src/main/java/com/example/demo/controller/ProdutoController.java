package com.example.demo.controller;

import com.example.demo.dto.VendaRequest;
import com.example.demo.dto.EstoqueRequest;
import com.example.demo.entity.Produto;
import com.example.demo.service.EstoqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final EstoqueService estoqueService;

    public ProdutoController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping("/{id}/venda")
    public ResponseEntity<Produto> realizarVenda(
            @PathVariable Long id,
            @RequestBody VendaRequest request) {

        Produto produto = estoqueService.realizarVenda(id, request.qtd());
        return ResponseEntity.ok(produto);
    }

    @PutMapping("/{id}/estoque")
    public ResponseEntity<Produto> atualizarEstoque(
            @PathVariable Long id,
            @RequestBody EstoqueRequest request) {

        Produto produto = estoqueService.atualizarEstoque(id, request.qtd());
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/{id}/estoque")
    public ResponseEntity<Produto> consultarEstoque(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.consultarEstoque(id));
    }

    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody Produto produto) {
        Produto novoProduto = estoqueService.criarProduto(produto);
        // Retorna 201 Created
        return ResponseEntity.status(201).body(novoProduto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        estoqueService.deletarProduto(id);
        // Retorna 204 No Content (sucesso sem corpo de resposta)
        return ResponseEntity.noContent().build();
    }
}
