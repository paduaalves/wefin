package com.wefin.controller;

import com.wefin.dto.ConversaoRequestDTO;
import com.wefin.dto.ConversaoResponseDTO;
import com.wefin.service.ConversaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversao")
public class ConversaoController {

    private final ConversaoService conversaoService;

    public ConversaoController(ConversaoService conversaoService) {
        this.conversaoService = conversaoService;
    }

    @Operation(
            summary = " Converter produtos entre moedas",
            description = "Realiza a conversão de produtos levando em consideração as moedas de origem/destino e o reino selecionado."
    )
    @GetMapping("/converter")
    public ResponseEntity<ConversaoResponseDTO> converter(
            @Parameter(description = "ID do produto a ser convertido", example = "1")
            @RequestParam("produtoId") Long produtoId,

            @Parameter(description = "ID do reino onde a conversão ocorre", example = "2")
            @RequestParam("reinoId") Long reinoId,

            @Parameter(description = "ID da moeda de origem", example = "1")
            @RequestParam("moedaOrigemId") Long moedaOrigemId,

            @Parameter(description = "ID da moeda de destino", example = "2")
            @RequestParam("moedaDestinoId") Long moedaDestinoId,

            @Parameter(description = "Quantidade do produto a ser convertido", example = "10")
            @RequestParam("quantidade") double quantidade) {

        ConversaoRequestDTO requestDTO = new ConversaoRequestDTO();
        requestDTO.setProdutoId(produtoId);
        requestDTO.setReinoId(reinoId);
        requestDTO.setMoedaOrigemId(moedaOrigemId);
        requestDTO.setMoedaDestinoId(moedaDestinoId);
        requestDTO.setQuantidade(quantidade);

        ConversaoResponseDTO responseDTO = conversaoService.converter(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}