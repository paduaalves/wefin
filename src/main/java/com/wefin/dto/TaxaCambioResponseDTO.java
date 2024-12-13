package com.wefin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TaxaCambioResponseDTO {
    @Schema(description = "ID da moeda de origem da taxa de câmbio", example = "1")
    private String moedaOrigem;

    @Schema(description = "ID da moeda de destino da taxa de câmbio", example = "2")
    private String moedaDestino;

    @Schema(description = "Valor atual da taxa de câmbio", example = "2.5")
    private BigDecimal valorAtual;

    @Schema(description = "Status indicando se a taxa está ativa", example = "true")
    private boolean ativa;

    @Schema(description = "Data de ativação da taxa de câmbio", example = "12/12/2024 15:47:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataAtivacao;

    @Schema(description = "Data de desativação da taxa de câmbio, se aplicável", example = "12/12/2024 21:05:11",
            type = "string",
            format = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataDesativacao;
}
