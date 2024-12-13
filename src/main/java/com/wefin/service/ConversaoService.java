package com.wefin.service;

import com.wefin.dto.ConversaoRequestDTO;
import com.wefin.dto.ConversaoResponseDTO;
import com.wefin.model.Produto;
import com.wefin.model.Reino;
import com.wefin.model.TaxaCambio;
import com.wefin.repository.ProdutoRepository;
import com.wefin.repository.ReinoRepository;
import com.wefin.strategy.ConversaoStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;


@Service
public class ConversaoService {

    private final Map<String, ConversaoStrategy> strategies;
    private final ProdutoRepository produtoRepository;
    private final ReinoRepository reinoRepository;
    private final TaxaCambioService taxaCambioService;

    @Autowired
    public ConversaoService(Map<String, ConversaoStrategy> strategies,
                            ProdutoRepository produtoRepository,
                            ReinoRepository reinoRepository,
                            TaxaCambioService taxaCambioService) {
        this.strategies = strategies;
        this.produtoRepository = produtoRepository;
        this.reinoRepository = reinoRepository;
        this.taxaCambioService = taxaCambioService;
    }

    public ConversaoResponseDTO converter(ConversaoRequestDTO request) {
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado para o ID: " + request.getProdutoId()));

        Reino reino = reinoRepository.findById(request.getReinoId())
                .orElseThrow(() -> new IllegalArgumentException("Reino não encontrado para o ID: " + request.getReinoId()));

        TaxaCambio taxaCambio = taxaCambioService.obterTaxaAtiva(
                request.getMoedaOrigemId(), request.getMoedaDestinoId()
        );

        ConversaoStrategy strategy = strategies.get(produto.getNome().toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Produto não suportado: " + produto.getNome());
        }

        BigDecimal precoFinal = strategy.converter(
                request.getQuantidade(),
                produto,
                taxaCambio.getValorAtual(),
                reino
        );

        ConversaoResponseDTO response = new ConversaoResponseDTO();
        response.setProduto(produto.getNome());
        response.setPrecoBase(produto.getPrecoBase());
        response.setQuantidadeOriginal(request.getQuantidade());
        response.setPrecoFinal(precoFinal);
        response.setMoedaOrigem(taxaCambio.getMoedaOrigem().getNome());
        response.setMoedaDestino(taxaCambio.getMoedaDestino().getNome());
        response.setReino(reino.getNome());
        response.setDataConversao(LocalDateTime.now());
        return response;
    }
}

