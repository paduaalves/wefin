package com.wefin.controller;


import com.wefin.dto.AtivarNovaTaxaRequestDTO;
import com.wefin.dto.TaxaCambioResponseDTO;
import com.wefin.model.TaxaCambio;
import com.wefin.service.TaxaCambioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/taxa-cambio")
public class TaxaCambioController {

    private final TaxaCambioService taxaCambioService;

    public TaxaCambioController(TaxaCambioService taxaCambioService) {
        this.taxaCambioService = taxaCambioService;
    }

    @Operation(summary = "Obter taxa ativa", description = "Obtém a taxa de câmbio ativa entre moedas de origem e destino")
    @GetMapping("/ativa")
    public ResponseEntity<TaxaCambioResponseDTO> obterTaxaAtiva(
            @Parameter(description = "ID da moeda de origem", example = "1") @RequestParam Long moedaOrigemId,
            @Parameter(description = "ID da moeda de destino", example = "2") @RequestParam Long moedaDestinoId) {
        TaxaCambio taxaCambio = taxaCambioService.obterTaxaAtiva(moedaOrigemId, moedaDestinoId);
        TaxaCambioResponseDTO responseDTO = new TaxaCambioResponseDTO();
        responseDTO.setValorAtual(taxaCambio.getValorAtual());
        responseDTO.setAtiva(taxaCambio.isAtiva());
        responseDTO.setDataAtivacao(taxaCambio.getDataAtivacao());
        responseDTO.setMoedaOrigem(taxaCambio.getMoedaOrigem().getNome());
        responseDTO.setMoedaDestino(taxaCambio.getMoedaDestino().getNome());
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Ativar nova taxa de câmbio", description = "Ativa uma nova taxa de câmbio informando valores, moedas e status")
    @PostMapping("/ativar")
    public ResponseEntity<TaxaCambio> ativarNovaTaxa(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Informações para ativação de uma nova taxa de câmbio")
            @RequestBody @Valid AtivarNovaTaxaRequestDTO request) {
        TaxaCambio taxaCambio = taxaCambioService.ativarNovaTaxa(
                request.getValorAtual(),
                request.getMoedaOrigemId(),
                request.getMoedaDestinoId()
        );
        return ResponseEntity.ok(taxaCambio);
    }

    @Operation(summary = "Consultar histórico de taxas", description = "Consulta o histórico de taxas de câmbio com filtros opcionais")
    @GetMapping("/historico")
    public ResponseEntity<Page<TaxaCambioResponseDTO>> consultarHistoricoTaxas(
            @Parameter(description = "Status da taxa (ativa/inativa)", example = "true")
            @RequestParam(value = "ativo", required = false) Boolean ativo,
            @Parameter(description = "ID da moeda de origem para filtrar")
            @RequestParam(value = "moedaOrigemId", required = false) Long moedaOrigemId,
            @Parameter(description = "Número da página", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<TaxaCambio> taxaCambioPage = taxaCambioService.consultarHistorico(ativo, moedaOrigemId, PageRequest.of(page, size));
        Page<TaxaCambioResponseDTO> response = taxaCambioPage.map(t -> {
            TaxaCambioResponseDTO dto = new TaxaCambioResponseDTO();
            dto.setValorAtual(t.getValorAtual());
            dto.setAtiva(t.isAtiva());
            dto.setDataAtivacao(t.getDataAtivacao());
            dto.setMoedaOrigem(t.getMoedaOrigem().getNome());
            dto.setMoedaDestino(t.getMoedaDestino().getNome());
            return dto;
        });
        return ResponseEntity.ok(response);
    }

}
