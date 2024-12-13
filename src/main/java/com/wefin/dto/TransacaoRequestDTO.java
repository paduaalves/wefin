package com.wefin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class TransacaoRequestDTO {
    @Schema(description = "ID do produto a ser transacionado", example = "1")
    @NotNull(message = "O valor do ID do produto é obrigatório")
    private Long produtoId;

    @Schema(description = "ID do reino relacionado à transação", example = "2")
    @NotNull(message = "O valor do ID do reino é obrigatório")
    private Long reinoId;

    @Schema(description = "ID da moeda de origem", example = "1")
    @NotNull(message = "O valor do ID da moeda de origem é obrigatório")
    private Long moedaOrigemId;

    @Schema(description = "ID da moeda de destino", example = "2")
    @NotNull(message = "O valor do ID da moeda de destino é obrigatório")
    private Long moedaDestinoId;

    @Schema(description = "Quantidade do produto", example = "10.5")
    @NotNull(message = "A quantidade é obrigatória")
    private double quantidade;
}