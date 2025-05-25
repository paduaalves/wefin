package com.wefin;

import com.wefin.model.Moeda;
import com.wefin.model.TaxaCambio;
import com.wefin.repository.MoedaRepository;
import com.wefin.repository.TaxaCambioRepository;
import com.wefin.service.TaxaCambioService;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaxaCambioServiceTest {

    @Mock
    private TaxaCambioRepository taxaCambioRepository;

    @Mock
    private MoedaRepository moedaRepository;

    @InjectMocks
    private TaxaCambioService taxaCambioService;

    private Moeda moedaOrigem;
    private Moeda moedaDestino;
    private TaxaCambio taxaCambioAtiva;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        moedaOrigem = new Moeda();
        moedaOrigem.setId(1L);
        moedaOrigem.setNome("Moeda Origem");
        // Removido setSimbolo que não existe na classe Moeda

        moedaDestino = new Moeda();
        moedaDestino.setId(2L);
        moedaDestino.setNome("Moeda Destino");
        // Removido setSimbolo que não existe na classe Moeda

        taxaCambioAtiva = new TaxaCambio();
        taxaCambioAtiva.setId(1L);
        taxaCambioAtiva.setValorAtual(BigDecimal.valueOf(2.5));
        taxaCambioAtiva.setAtiva(true);
        taxaCambioAtiva.setDataAtivacao(LocalDateTime.now().minusDays(1));
        taxaCambioAtiva.setMoedaOrigem(moedaOrigem);
        taxaCambioAtiva.setMoedaDestino(moedaDestino);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testObterTaxaAtiva_QuandoExiste_RetornaTaxaAtiva() {
        // Arrange
        when(taxaCambioRepository.findByAtivaTrueAndMoedaOrigemIdAndMoedaDestinoId(1L, 2L))
                .thenReturn(Optional.of(taxaCambioAtiva));

        // Act
        TaxaCambio result = taxaCambioService.obterTaxaAtiva(1L, 2L);

        // Assert
        assertNotNull(result);
        assertEquals(taxaCambioAtiva.getId(), result.getId());
        assertEquals(taxaCambioAtiva.getValorAtual(), result.getValorAtual());
        assertTrue(result.isAtiva());
        verify(taxaCambioRepository).findByAtivaTrueAndMoedaOrigemIdAndMoedaDestinoId(1L, 2L);
    }

    @Test
    void testObterTaxaAtiva_QuandoNaoExiste_LancaException() {
        // Arrange
        when(taxaCambioRepository.findByAtivaTrueAndMoedaOrigemIdAndMoedaDestinoId(1L, 2L))
                .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taxaCambioService.obterTaxaAtiva(1L, 2L)
        );

        assertEquals("Nenhuma taxa de câmbio ativa encontrada para as moedas informadas.", exception.getMessage());
        verify(taxaCambioRepository).findByAtivaTrueAndMoedaOrigemIdAndMoedaDestinoId(1L, 2L);
    }

    @Test
    void testAtivarNovaTaxa_QuandoParametrosValidos_CriaNovaTaxaAtiva() {
        // Arrange
        BigDecimal novoValor = BigDecimal.valueOf(3.0);

        when(moedaRepository.findById(1L)).thenReturn(Optional.of(moedaOrigem));
        when(moedaRepository.findById(2L)).thenReturn(Optional.of(moedaDestino));
        when(taxaCambioRepository.findByAtivaTrueAndMoedaOrigemIdAndMoedaDestinoId(1L, 2L))
                .thenReturn(Optional.of(taxaCambioAtiva));
        when(taxaCambioRepository.save(any(TaxaCambio.class))).thenAnswer(invocation -> {
            TaxaCambio taxa = invocation.getArgument(0);
            if (taxa.getId() == null) {
                taxa.setId(2L); // Simulando ID para nova taxa
            }
            return taxa;
        });

        // Act
        TaxaCambio result = taxaCambioService.ativarNovaTaxa(novoValor, 1L, 2L);

        // Assert
        assertNotNull(result);
        assertEquals(novoValor, result.getValorAtual());
        assertTrue(result.isAtiva());
        assertNotNull(result.getDataAtivacao());
        assertEquals(moedaOrigem, result.getMoedaOrigem());
        assertEquals(moedaDestino, result.getMoedaDestino());

        // Verifica se a taxa anterior foi desativada
        verify(taxaCambioRepository, times(2)).save(any(TaxaCambio.class));
        verify(moedaRepository).findById(1L);
        verify(moedaRepository).findById(2L);
        verify(taxaCambioRepository).findByAtivaTrueAndMoedaOrigemIdAndMoedaDestinoId(1L, 2L);
    }

    @Test
    void testAtivarNovaTaxa_QuandoMoedaOrigemNaoEncontrada_LancaException() {
        // Arrange
        when(moedaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taxaCambioService.ativarNovaTaxa(BigDecimal.ONE, 1L, 2L)
        );

        assertEquals("Moeda origem não encontrada.", exception.getMessage());
        verify(moedaRepository).findById(1L);
        verifyNoMoreInteractions(moedaRepository, taxaCambioRepository);
    }

    @Test
    void testAtivarNovaTaxa_QuandoMoedaDestinoNaoEncontrada_LancaException() {
        // Arrange
        when(moedaRepository.findById(1L)).thenReturn(Optional.of(moedaOrigem));
        when(moedaRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taxaCambioService.ativarNovaTaxa(BigDecimal.ONE, 1L, 2L)
        );

        assertEquals("Moeda destino não encontrada.", exception.getMessage());
        verify(moedaRepository).findById(1L);
        verify(moedaRepository).findById(2L);
        verifyNoMoreInteractions(taxaCambioRepository);
    }

    @Test
    void testConsultarHistorico_QuandoAtivoEMoedaOrigemInformados_RetornaFiltradoPorAmbos() {
        // Arrange
        Page<TaxaCambio> expected = new PageImpl<>(Collections.singletonList(taxaCambioAtiva));
        when(taxaCambioRepository.findByAtivaAndMoedaOrigemId(true, 1L, pageable)).thenReturn(expected);

        // Act
        Page<TaxaCambio> result = taxaCambioService.consultarHistorico(true, 1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(taxaCambioAtiva, result.getContent().get(0));
        verify(taxaCambioRepository).findByAtivaAndMoedaOrigemId(true, 1L, pageable);
    }

    @Test
    void testConsultarHistorico_QuandoApenasAtivoInformado_RetornaFiltradoPorAtivo() {
        // Arrange
        Page<TaxaCambio> expected = new PageImpl<>(Collections.singletonList(taxaCambioAtiva));
        when(taxaCambioRepository.findByAtiva(true, pageable)).thenReturn(expected);

        // Act
        Page<TaxaCambio> result = taxaCambioService.consultarHistorico(true, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taxaCambioRepository).findByAtiva(true, pageable);
    }

    @Test
    void testConsultarHistorico_QuandoApenasMoedaOrigemInformada_RetornaFiltradoPorMoedaOrigem() {
        // Arrange
        Page<TaxaCambio> expected = new PageImpl<>(Collections.singletonList(taxaCambioAtiva));
        when(taxaCambioRepository.findByMoedaOrigemId(1L, pageable)).thenReturn(expected);

        // Act
        Page<TaxaCambio> result = taxaCambioService.consultarHistorico(null, 1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taxaCambioRepository).findByMoedaOrigemId(1L, pageable);
    }

    @Test
    void testConsultarHistorico_QuandoNenhumFiltroInformado_RetornaTodos() {
        // Arrange
        Page<TaxaCambio> expected = new PageImpl<>(Collections.singletonList(taxaCambioAtiva));
        when(taxaCambioRepository.findAll(pageable)).thenReturn(expected);

        // Act
        Page<TaxaCambio> result = taxaCambioService.consultarHistorico(null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taxaCambioRepository).findAll(pageable);
    }
}