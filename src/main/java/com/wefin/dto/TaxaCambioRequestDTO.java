package com.wefin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaxaCambioRequestDTO {
    @Schema(description = "ID da moeda de origem da taxa de câmbio", example = "1")
    @NotNull(message = "O valor do ID da moeda de origem é obrigatório")
    private Long moedaOrigemId;

    @Schema(description = "ID da moeda de destino da taxa de câmbio", example = "2")
    @NotNull(message = "O valor do ID da moeda de destino é obrigatório")
    private Long moedaDestinoId;

}
