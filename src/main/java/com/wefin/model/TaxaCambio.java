package com.wefin.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "taxa_cambio")
public class TaxaCambio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valorAtual;

    @Column(nullable = false)
    private boolean ativa;

    private LocalDateTime dataAtivacao;
    private LocalDateTime dataDesativacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moeda_origem_id", nullable = false)
    private Moeda moedaOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moeda_destino_id", nullable = false)
    private Moeda moedaDestino;
}