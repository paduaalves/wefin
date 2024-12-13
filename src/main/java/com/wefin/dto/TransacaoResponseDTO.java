package com.wefin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wefin.model.Transacao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransacaoResponseDTO {
    @Schema(description = "ID da transação realizada", example = "1001")
    private Long id;

    @Schema(description = "Nome do produto envolvido na transação", example = "Pele")
    private String produto;

    @Schema(description = "Nome da moeda de origem da transação", example = "Ouro Real")
    private String moedaOrigem;

    @Schema(description = "Nome da moeda de destino da transação", example = "Tibar")
    private String moedaDestino;

    @Schema(description = "Nome do reino associado à transação", example = "Wefin")
    private String reino;

    @Schema(description = "Valor total da transação", example = "262.50")
    private BigDecimal valorTransacao;

    @Schema(description = "Data e hora em que a transação foi realizada", example = "12/12/2024 21:05:11",
            type = "string",
            format = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataTransacao;


    public TransacaoResponseDTO(Transacao transacao) {
        this.produto = transacao.getProduto().getNome();
        this.moedaOrigem = transacao.getMoedaOrigem().getNome();
        this.moedaDestino = transacao.getMoedaDestino().getNome();
        this.valorTransacao = transacao.getValorTransacao();
        this.reino = transacao.getReino().getNome();
        this.dataTransacao = transacao.getDataTransacao();
    }

    public TransacaoResponseDTO() {

    }
}
