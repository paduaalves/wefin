package com.wefin.controller;


import com.wefin.dto.TransacaoRequestDTO;
import com.wefin.dto.TransacaoResponseDTO;
import com.wefin.model.Transacao;
import com.wefin.service.TransacaoService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;


import java.time.LocalDate;

@RestController
@RequestMapping("/api/transacao")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    @Operation(summary = "Realizar transações", description = "Permite realizar transações de produtos entre moedas diferentes.")
    public ResponseEntity<TransacaoResponseDTO> realizarTransacao(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados da transação a ser realizada")
            @RequestBody @Valid TransacaoRequestDTO request) {
        TransacaoResponseDTO response = transacaoService.realizarTransacao(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar histórico de transações", description = "Permite consultar o histórico de transações com filtros opcionais e paginação.")
    @GetMapping("/historico")
    public ResponseEntity<Page<TransacaoResponseDTO>> consultarHistoricoTransacoes(
            @Parameter(description = "ID do Produto para filtrar")
            @RequestParam(value = "produtoId", required = false) Long produtoId,

            @Parameter(description = "ID da Moeda de Origem para filtrar")
            @RequestParam(value = "moedaOrigemId", required = false) Long moedaOrigemId,

            @Parameter(description = "ID da Moeda de Destino para filtrar")
            @RequestParam(value = "moedaDestinoId", required = false) Long moedaDestinoId,

            @Parameter(description = "ID do Reino para filtrar")
            @RequestParam(value = "reinoId", required = false) Long reinoId,

            @Parameter(description = "Data específica para filtrar (yyyy-MM-dd)")
            @RequestParam(value = "data", required = false) LocalDate data,

            @Parameter(description = "Número da página (começa em 0)")
            @RequestParam(value = "page", defaultValue = "0") int page,

            @Parameter(description = "Tamanho da página")
            @RequestParam(value = "size", defaultValue = "10") int size) {


        Page<Transacao> transacoes = transacaoService.consultarHistorico(produtoId, moedaOrigemId, moedaDestinoId, reinoId, data, PageRequest.of(page, size));

        Page<TransacaoResponseDTO> response = transacoes.map(t -> {
            TransacaoResponseDTO dto = new TransacaoResponseDTO();
            dto.setProduto(t.getProduto().getNome());
            dto.setMoedaOrigem(t.getMoedaOrigem().getNome());
            dto.setMoedaDestino(t.getMoedaDestino().getNome());
            dto.setReino(t.getReino().getNome());
            dto.setDataTransacao(t.getDataTransacao());
            dto.setValorTransacao(t.getValorTransacao());
            return dto;
        });

        return ResponseEntity.ok(response);
    }
}