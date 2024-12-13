package com.wefin.service;


import com.wefin.dto.TransacaoRequestDTO;
import com.wefin.dto.TransacaoResponseDTO;
import com.wefin.model.*;
import com.wefin.repository.MoedaRepository;
import com.wefin.repository.ProdutoRepository;
import com.wefin.repository.ReinoRepository;
import com.wefin.repository.TransacaoRepository;
import com.wefin.strategy.ConversaoStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransacaoService {

    private final Map<String, ConversaoStrategy> strategies;
    private final ReinoRepository reinoRepository;
    private final TransacaoRepository transacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final TaxaCambioService taxaCambioService;

    @Autowired
    public TransacaoService(Map<String, ConversaoStrategy> strategies,
                            ReinoRepository reinoRepository,
                            TransacaoRepository transacaoRepository,
                            ProdutoRepository produtoRepository,
                            TaxaCambioService taxaCambioService) {
        this.strategies = strategies;
        this.reinoRepository = reinoRepository;
        this.transacaoRepository = transacaoRepository;
        this.produtoRepository = produtoRepository;
        this.taxaCambioService = taxaCambioService;
    }

    @Transactional
    public TransacaoResponseDTO realizarTransacao(TransacaoRequestDTO request) {
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        Reino reino = reinoRepository.findById(request.getReinoId())
                .orElseThrow(() -> new IllegalArgumentException("Reino não encontrado"));

        // Obter a taxa de câmbio ativa com IDs das moedas
        TaxaCambio taxaCambio = taxaCambioService.obterTaxaAtiva(
                request.getMoedaOrigemId(), request.getMoedaDestinoId()
        );

        ConversaoStrategy strategy = strategies.get(produto.getNome().toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Produto não suportado: " + produto.getNome());
        }

        BigDecimal valorTransacao = strategy.converter(
                request.getQuantidade(),
                produto,
                taxaCambio.getValorAtual(),
                reino
        );

        Transacao transacao = new Transacao();
        transacao.setProduto(produto);
        transacao.setMoedaOrigem(taxaCambio.getMoedaOrigem());
        transacao.setMoedaDestino(taxaCambio.getMoedaDestino());
        transacao.setValorTransacao(valorTransacao);
        transacao.setReino(reino);
        transacao.setQuantidade(request.getQuantidade());
        transacao.setDataTransacao(LocalDateTime.now());

        Transacao savedTransacao = transacaoRepository.save(transacao);

        TransacaoResponseDTO response = new TransacaoResponseDTO(savedTransacao);
        return response;
    }

    public Page<Transacao> consultarHistorico(Long produtoId, Long moedaOrigemId, Long moedaDestinoId, Long reinoId, LocalDate data, Pageable pageable) {
        if (produtoId != null || moedaOrigemId != null || moedaDestinoId != null || reinoId != null || data != null) {
            return transacaoRepository.findByFilters(produtoId, moedaOrigemId, moedaDestinoId, reinoId, data, pageable);
        }
        return transacaoRepository.findAll(pageable);
    }
}