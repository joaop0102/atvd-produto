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
            if (repository.count() == 0) {
                repository.save(new Produto("Notebook", 10));
                repository.save(new Produto("Mouse", 25));
                repository.save(new Produto("Teclado", 15));
            }
        };
    }
}
