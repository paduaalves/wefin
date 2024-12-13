package com.wefin.repository;

import com.wefin.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    @Query("SELECT t FROM Transacao t WHERE (:produtoId IS NULL OR t.produto.id = :produtoId) AND " +
            "(:moedaOrigemId IS NULL OR t.moedaOrigem.id = :moedaOrigemId) AND" +
            "(:moedaDestinoId IS NULL OR t.moedaDestino.id = :moedaDestinoId) AND" +
            "(:reinoId IS NULL OR t.reino.id = :reinoId) AND" +
            "(:data IS NULL OR DATE(t.dataTransacao) = :data)")
    Page<Transacao> findByFilters(@Param("produtoId") Long produtoId,
                                  @Param("moedaOrigemId") Long moedaOrigemId,
                                  @Param("moedaDestinoId") Long moedaDestinoId,
                                  @Param("reinoId") Long reinoId,
                                  @Param("data") LocalDate data,
                                  Pageable pageable);

}