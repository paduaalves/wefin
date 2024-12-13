package com.wefin.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Reino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private double fatorReino;

    @OneToOne
    @JoinColumn(name = "moeda_id")
    private Moeda moeda;
}
