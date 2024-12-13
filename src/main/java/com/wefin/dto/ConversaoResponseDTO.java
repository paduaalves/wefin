package com.wefin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ConversaoResponseDTO {
    @Schema(description = "Nome do produto convertido", example = "Pele")
    private String produto;

    @Schema(description = "Quantidade original informada para a conversão", example = "10.5")
    private double quantidadeOriginal;

    @Schema(description = "Quantidade convertida após a conversão", example = "26.25")
    private double quantidadeConvertida;

    @Schema(description = "Preço Base antes da conversão", example = "100.50")
    private BigDecimal precoBase;

    @Schema(description = "Preço final após conversão considerando taxas e fatores", example = "262.50")
    private BigDecimal precoFinal;

    @Schema(description = "Nome da moeda de origem da conversão", example = "Ouro Real")
    private String moedaOrigem;

    @Schema(description = "Nome da moeda de destino da conversão", example = "Tibar")
    private String moedaDestino;

    @Schema(description = "Nome do reino associado à conversão", example = "Wefin")
    private String reino;

    @Schema(description = "Data e hora em que a conversão foi realizada", example = "12/12/2024 21:05:11",
            type = "string",
            format = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataConversao;
}
