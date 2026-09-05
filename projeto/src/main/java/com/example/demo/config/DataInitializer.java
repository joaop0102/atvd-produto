package com.example.demo.config;

import com.example.demo.entity.Produto;
import com.example.demo.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner carregarProdutos(ProdutoRepository repository) {
        return args -> {
            criarSeNaoExistir(repository, "Notebook", 10);
            criarSeNaoExistir(repository, "Mouse", 25);
            criarSeNaoExistir(repository, "Teclado", 15);
        };
    }

    private void criarSeNaoExistir(ProdutoRepository repository, String nome, int qtd) {
        repository.findByNome(nome)
                .orElseGet(() -> repository.save(new Produto(nome, qtd)));
    }
}
