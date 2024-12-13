package com.wefin.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "moeda")
public class Moeda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    //@Column(nullable = false)
    //private double taxaCambio;

    //@OneToMany(mappedBy = "moedaOrigem", fetch = FetchType.LAZY)
    //private List<TaxaCambio> taxasCambioAtivas;


}