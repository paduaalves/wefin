package com.wefin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AtivarNovaTaxaRequestDTO {
    @NotNull(message = "O valor atual da taxa é obrigatório")
    @Schema(description = "Valor atual da taxa de câmbio", example = "2.5")
    private BigDecimal valorAtual;

    @NotNull(message = "O ID da moeda de origem é obrigatório")
    @Schema(description = "ID da moeda de origem", example = "1")
    private Long moedaOrigemId;

    @NotNull(message = "O ID da moeda de destino é obrigatório")
    @Schema(description = "ID da moeda de destino", example = "2")
    private Long moedaDestinoId;
}
