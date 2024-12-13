package com.wefin.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transacao")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moeda_origem_id", nullable = false)
    private Moeda moedaOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moeda_destino_id", nullable = false)
    private Moeda moedaDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reino_id", nullable = false)
    private Reino reino;

    @Column(nullable = false)
    private double quantidade;

    @Column(nullable = false)
    private BigDecimal valorTransacao;

    @Column(nullable = false)
    private LocalDateTime dataTransacao;
}
