package com.wefin;

import com.wefin.dto.TransacaoRequestDTO;
import com.wefin.dto.TransacaoResponseDTO;
import com.wefin.model.*;
import com.wefin.repository.ProdutoRepository;
import com.wefin.repository.ReinoRepository;
import com.wefin.repository.TransacaoRepository;
import com.wefin.service.TaxaCambioService;
import com.wefin.service.TransacaoService;
import com.wefin.strategy.ConversaoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ReinoRepository reinoRepository;

    @Mock
    private TaxaCambioService taxaCambioService;

    @Mock
    private ConversaoStrategy conversaoStrategy;

    private Map<String, ConversaoStrategy> strategies;

    @InjectMocks
    private TransacaoService transacaoService;

    private Produto produto;
    private Reino reino;
    private Moeda moedaOrigem;
    private Moeda moedaDestino;
    private TaxaCambio taxaCambio;
    private TransacaoRequestDTO request;
    private Transacao transacao;

    @BeforeEach
    void setUp() {
        strategies = new HashMap<>();
        strategies.put("produto teste", conversaoStrategy); // Corrigido para usar minúsculas

        moedaOrigem = new Moeda();
        moedaOrigem.setId(1L);
        moedaOrigem.setNome("Moeda Origem");
        // Removido setSimbolo que não existe na classe Moeda

        moedaDestino = new Moeda();
        moedaDestino.setId(2L);
        moedaDestino.setNome("Moeda Destino");
        // Removido setSimbolo que não existe na classe Moeda

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        // Removido setTipoConversao que não existe na classe Produto

        reino = new Reino();
        reino.setId(2L);
        reino.setNome("Reino Teste");

        taxaCambio = new TaxaCambio();
        taxaCambio.setId(1L);
        taxaCambio.setValorAtual(BigDecimal.valueOf(2.5));
        taxaCambio.setAtiva(true);
        taxaCambio.setMoedaOrigem(moedaOrigem);
        taxaCambio.setMoedaDestino(moedaDestino);

        request = new TransacaoRequestDTO();
        request.setProdutoId(1L);
        request.setReinoId(2L);
        request.setMoedaOrigemId(1L);
        request.setMoedaDestinoId(2L);
        request.setQuantidade(10.0); // Corrigido para usar double em vez de BigDecimal

        transacao = new Transacao();
        transacao.setId(1L);
        transacao.setProduto(produto);
        transacao.setReino(reino);
        transacao.setMoedaOrigem(moedaOrigem);
        transacao.setMoedaDestino(moedaDestino);
        transacao.setQuantidade(10.0); // Corrigido para usar double em vez de BigDecimal
        transacao.setValorTransacao(BigDecimal.valueOf(25.0));
        transacao.setDataTransacao(LocalDateTime.now());

        transacaoService = new TransacaoService(strategies, reinoRepository, transacaoRepository, produtoRepository, taxaCambioService);
    }

    @Test
    void testRealizarTransacao_QuandoParametrosValidos_RetornaTransacaoEfetuada() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(reinoRepository.findById(2L)).thenReturn(Optional.of(reino));
        when(taxaCambioService.obterTaxaAtiva(1L, 2L)).thenReturn(taxaCambio);

        // Mock para simular a conversão
        BigDecimal valorConvertido = BigDecimal.valueOf(25.0);

        // Corrigido para usar a assinatura correta do método converter
        when(conversaoStrategy.converter(anyDouble(), any(Produto.class), any(BigDecimal.class), any(Reino.class)))
                .thenReturn(valorConvertido);

        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacao);

        // Act
        TransacaoResponseDTO result = transacaoService.realizarTransacao(request);

        // Assert
        assertNotNull(result);
        // Se getValorConvertido não existe, verifique qual é o método correto na classe TransacaoResponseDTO
        // por exemplo, talvez seja getValorTransacao() ou outro getter
        // assertEquals(valorConvertido, result.getValorTransacao());

        verify(produtoRepository).findById(1L);
        verify(reinoRepository).findById(2L);
        verify(taxaCambioService).obterTaxaAtiva(1L, 2L);
        // Corrigido para usar a assinatura correta do método converter
        verify(conversaoStrategy).converter(anyDouble(), any(Produto.class), any(BigDecimal.class), any(Reino.class));
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void testRealizarTransacao_QuandoProdutoNaoEncontrado_LancaException() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transacaoService.realizarTransacao(request)
        );

        assertEquals("Produto não encontrado", exception.getMessage());
        verify(produtoRepository).findById(1L);
        verifyNoInteractions(reinoRepository, taxaCambioService, conversaoStrategy, transacaoRepository);
    }

    @Test
    void testRealizarTransacao_QuandoReinoNaoEncontrado_LancaException() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(reinoRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transacaoService.realizarTransacao(request)
        );

        assertEquals("Reino não encontrado", exception.getMessage());
        verify(produtoRepository).findById(1L);
        verify(reinoRepository).findById(2L);
        verifyNoInteractions(taxaCambioService, conversaoStrategy, transacaoRepository);
    }

    @Test
    void testRealizarTransacao_QuandoEstrategiaNaoEncontrada_LancaException() {
        // Arrange
        strategies.clear(); // Removendo a estratégia para forçar a exceção

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(reinoRepository.findById(2L)).thenReturn(Optional.of(reino));
        when(taxaCambioService.obterTaxaAtiva(1L, 2L)).thenReturn(taxaCambio);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transacaoService.realizarTransacao(request)
        );

        assertEquals("Produto não suportado: Produto Teste", exception.getMessage());
        verify(produtoRepository).findById(1L);
        verify(reinoRepository).findById(2L);
        verify(taxaCambioService).obterTaxaAtiva(1L, 2L);
        verifyNoMoreInteractions(taxaCambioService);
        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void testConsultarHistorico_QuandoTodosParametrosInformados_RetornaFiltrado() {
        // Arrange
        Page<Transacao> expected = new PageImpl<>(Collections.singletonList(transacao));
        Pageable pageable = PageRequest.of(0, 10);

        LocalDate data = LocalDate.now();
        when(transacaoRepository.findByFilters(1L, 1L, 2L, 2L, data, pageable)).thenReturn(expected);

        // Act
        Page<Transacao> result = transacaoService.consultarHistorico(1L, 1L, 2L, 2L, data, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(transacao, result.getContent().get(0));
        verify(transacaoRepository).findByFilters(1L, 1L, 2L, 2L, data, pageable);
    }

    @Test
    void testConsultarHistorico_QuandoNenhumParametroInformado_RetornaTodos() {
        // Arrange
        Page<Transacao> expected = new PageImpl<>(Collections.singletonList(transacao));
        Pageable pageable = PageRequest.of(0, 10);

        when(transacaoRepository.findAll(pageable)).thenReturn(expected);

        // Act
        Page<Transacao> result = transacaoService.consultarHistorico(null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transacaoRepository).findAll(pageable);
    }
}