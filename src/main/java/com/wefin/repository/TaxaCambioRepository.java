package com.wefin.repository;

import com.wefin.model.TaxaCambio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaxaCambioRepository extends JpaRepository<TaxaCambio, Long> {
    @Query("SELECT t FROM TaxaCambio t WHERE t.ativa = true AND t.moedaOrigem.id = :moedaOrigemId AND t.moedaDestino.id = :moedaDestinoId")
    Optional<TaxaCambio> findByAtivaTrueAndMoedaOrigemIdAndMoedaDestinoId(@Param("moedaOrigemId") Long moedaOrigemId,
                                                                          @Param("moedaDestinoId") Long moedaDestinoId);

    Page<TaxaCambio> findByAtiva(boolean ativa, Pageable pageable);

    Page<TaxaCambio> findByAtivaAndMoedaOrigemId(boolean ativa, Long moedaOrigemId, Pageable pageable);

    Page<TaxaCambio> findByMoedaOrigemId(Long moedaOrigemId, Pageable pageable);

}
