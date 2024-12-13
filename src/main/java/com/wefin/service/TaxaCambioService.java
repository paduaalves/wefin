package com.wefin.service;


import com.wefin.model.Moeda;
import com.wefin.model.TaxaCambio;
import com.wefin.repository.MoedaRepository;
import com.wefin.repository.TaxaCambioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TaxaCambioService {

    private final TaxaCambioRepository taxaCambioRepository;
    private final MoedaRepository moedaRepository;

    @Autowired
    public TaxaCambioService(TaxaCambioRepository taxaCambioRepository, MoedaRepository moedaRepository) {
        this.taxaCambioRepository = taxaCambioRepository;
        this.moedaRepository = moedaRepository;
    }

    public TaxaCambio obterTaxaAtiva(Long moedaOrigemId, Long moedaDestinoId) {
        return taxaCambioRepository.findByAtivaTrueAndMoedaOrigemIdAndMoedaDestinoId(moedaOrigemId, moedaDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("Nenhuma taxa de câmbio ativa encontrada para as moedas informadas."));
    }

    @Transactional
    public TaxaCambio ativarNovaTaxa(BigDecimal valorAtual, Long moedaOrigemId, Long moedaDestinoId) {
        // Buscar as moedas
        Moeda moedaOrigem = moedaRepository.findById(moedaOrigemId)
                .orElseThrow(() -> new IllegalArgumentException("Moeda origem não encontrada."));
        Moeda moedaDestino = moedaRepository.findById(moedaDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("Moeda destino não encontrada."));

        // Desativar taxa anterior, se houver
        TaxaCambio taxaAnterior = taxaCambioRepository.findByAtivaTrueAndMoedaOrigemIdAndMoedaDestinoId(moedaOrigemId, moedaDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("Nenhuma taxa de câmbio ativa encontrada para as moedas informadas."));

        taxaAnterior.setAtiva(false);
        taxaAnterior.setDataDesativacao(LocalDateTime.now());
        taxaCambioRepository.save(taxaAnterior);


        // Criar nova taxa
        TaxaCambio novaTaxa = new TaxaCambio();
        novaTaxa.setValorAtual(valorAtual);
        novaTaxa.setAtiva(true);
        novaTaxa.setDataAtivacao(LocalDateTime.now());
        novaTaxa.setMoedaOrigem(moedaOrigem);
        novaTaxa.setMoedaDestino(moedaDestino);
        return taxaCambioRepository.save(novaTaxa);
    }

    public Page<TaxaCambio> consultarHistorico(Boolean ativo, Long moedaOrigemId, Pageable pageable) {
        if (ativo != null && moedaOrigemId != null) {
            return taxaCambioRepository.findByAtivaAndMoedaOrigemId(ativo, moedaOrigemId, pageable);
        } else if (ativo != null) {
            return taxaCambioRepository.findByAtiva(ativo, pageable);
        } else if (moedaOrigemId != null) {
            return taxaCambioRepository.findByMoedaOrigemId(moedaOrigemId, pageable);
        }
        return taxaCambioRepository.findAll(pageable);
    }
}
