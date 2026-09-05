package com.example.demo.repository;

import com.example.demo.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByNome(String nome);

    /**
     * Realiza a baixa de forma atomica: so altera o estoque quando
     * a quantidade disponivel for suficiente.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Produto p
               set p.qtd = p.qtd - :quantidade
             where p.id = :id
               and p.qtd >= :quantidade
            """)
    int baixarEstoque(@Param("id") Long id, @Param("quantidade") Integer quantidade);
}
