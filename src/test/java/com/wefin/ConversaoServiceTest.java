package com.wefin;

import com.wefin.dto.ConversaoRequestDTO;
import com.wefin.dto.ConversaoResponseDTO;
import com.wefin.model.Moeda;
import com.wefin.model.Produto;
import com.wefin.model.Reino;
import com.wefin.model.TaxaCambio;
import com.wefin.repository.ProdutoRepository;
import com.wefin.repository.ReinoRepository;
import com.wefin.service.ConversaoService;
import com.wefin.service.TaxaCambioService;
import com.wefin.strategy.ConversaoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConversaoServiceTest {

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
    private ConversaoService conversaoService;

    @BeforeEach
    void setUp() {
        strategies = new HashMap<>();
        strategies.put("produto teste", conversaoStrategy); // Chave em lowercase como é usado na implementação
        conversaoService = new ConversaoService(strategies, produtoRepository, reinoRepository, taxaCambioService);
    }

    @Test
    void testConverter_QuandoParametrosValidos_RetornaConversaoEfetuada() {
        // Arrange
        ConversaoRequestDTO request = new ConversaoRequestDTO();
        request.setProdutoId(1L);
        request.setReinoId(2L);
        request.setMoedaOrigemId(3L);
        request.setMoedaDestinoId(4L);
        request.setQuantidade(10.0);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste"); // Corresponde a "produto teste" em lowercase no mapa
        produto.setPrecoBase(BigDecimal.valueOf(5.0)); // Necessário para response

        Reino reino = new Reino();
        reino.setId(2L);
        reino.setNome("Reino Teste");

        // Configurar objetos para o taxaCambio
        Moeda moedaOrigem = new Moeda();
        moedaOrigem.setId(3L);
        moedaOrigem.setNome("Moeda Origem");

        Moeda moedaDestino = new Moeda();
        moedaDestino.setId(4L);
        moedaDestino.setNome("Moeda Destino");

        TaxaCambio taxaCambio = new TaxaCambio();
        taxaCambio.setValorAtual(BigDecimal.valueOf(2.5));
        taxaCambio.setMoedaOrigem(moedaOrigem);
        taxaCambio.setMoedaDestino(moedaDestino);

        BigDecimal valorConvertido = BigDecimal.valueOf(25.0);

        // Mock dos repositórios
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(reinoRepository.findById(2L)).thenReturn(Optional.of(reino));
        when(taxaCambioService.obterTaxaAtiva(3L, 4L)).thenReturn(taxaCambio);
        when(conversaoStrategy.converter(anyDouble(), any(Produto.class), any(BigDecimal.class), any(Reino.class)))
                .thenReturn(valorConvertido);

        // Act
        ConversaoResponseDTO result = conversaoService.converter(request);

        // Assert
        assertNotNull(result);
        assertEquals(valorConvertido, result.getPrecoFinal()); // Usando o método correto baseado na implementação
        assertEquals("Produto Teste", result.getProduto());
        assertEquals(10.0, result.getQuantidadeOriginal(), 0.001);

        verify(produtoRepository).findById(1L);
        verify(reinoRepository).findById(2L);
        verify(taxaCambioService).obterTaxaAtiva(3L, 4L);
        verify(conversaoStrategy).converter(anyDouble(), any(Produto.class), any(BigDecimal.class), any(Reino.class));
    }

    @Test
    void testConverter_QuandoProdutoNaoEncontrado_LancaException() {
        // Arrange
        ConversaoRequestDTO request = new ConversaoRequestDTO();
        request.setProdutoId(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> conversaoService.converter(request)
        );

        // Atualizar a mensagem esperada para corresponder à implementação
        assertEquals("Produto não encontrado para o ID: 1", exception.getMessage());
        verify(produtoRepository).findById(1L);
        verifyNoInteractions(reinoRepository, taxaCambioService, conversaoStrategy);
    }

    @Test
    void testConverter_QuandoReinoNaoEncontrado_LancaException() {
        // Arrange
        ConversaoRequestDTO request = new ConversaoRequestDTO();
        request.setProdutoId(1L);
        request.setReinoId(2L);

        Produto produto = new Produto();
        produto.setId(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(reinoRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> conversaoService.converter(request)
        );

        // Atualizar a mensagem esperada para corresponder à implementação
        assertEquals("Reino não encontrado para o ID: 2", exception.getMessage());
        verify(produtoRepository).findById(1L);
        verify(reinoRepository).findById(2L);
        verifyNoInteractions(taxaCambioService, conversaoStrategy);
    }

    @Test
    void testConverter_QuandoEstrategiaNaoEncontrada_LancaException() {
        // Arrange
        ConversaoRequestDTO request = new ConversaoRequestDTO();
        request.setProdutoId(1L);
        request.setReinoId(2L);
        request.setMoedaOrigemId(3L);  // Adicionando estes IDs que são necessários
        request.setMoedaDestinoId(4L); // para a chamada de taxaCambioService

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Inexistente"); // Usando nome que não tem estratégia associada

        Reino reino = new Reino();
        reino.setId(2L);

        TaxaCambio taxaCambio = new TaxaCambio(); // Criando objeto para o mock
        taxaCambio.setValorAtual(BigDecimal.valueOf(2.5));

        // Configurando os mocks necessários
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(reinoRepository.findById(2L)).thenReturn(Optional.of(reino));
        when(taxaCambioService.obterTaxaAtiva(3L, 4L)).thenReturn(taxaCambio);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> conversaoService.converter(request)
        );

        assertEquals("Produto não suportado: Inexistente", exception.getMessage());
        verify(produtoRepository).findById(1L);
        verify(reinoRepository).findById(2L);
        verify(taxaCambioService).obterTaxaAtiva(3L, 4L); // Verificar que foi chamado
        verifyNoMoreInteractions(taxaCambioService);       // E nenhuma outra interação
    }
}